package co.anbora.labs.sqlfluff.ide.annotator

import co.anbora.labs.sqlfluff.ide.lang.psi.PsiFinderFlavor
import co.anbora.labs.sqlfluff.ide.notifications.LinterErrorNotification
import co.anbora.labs.sqlfluff.ide.quickFix.QuickFixFlavor
import co.anbora.labs.sqlfluff.ide.toolchain.LinterExecutionService
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import co.anbora.labs.sqlfluff.ide.widget.LinterStatusService
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile.Companion.DEFAULT_DIALECT
import co.anbora.labs.sqlfluff.lint.LinterConfig
import co.anbora.labs.sqlfluff.lint.checker.Problem
import co.anbora.labs.sqlfluff.lint.exception.LinterException
import co.anbora.labs.sqlfluff.lint.isSqlFileType
import co.anbora.labs.sqlfluff.lint.issue.SQL_FLUFF
import co.anbora.labs.sqlfluff.lint.processor.LintExecutorRequest
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiFile
import java.util.concurrent.CancellationException
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException

class LinterExternalAnnotator: ExternalAnnotator<LinterExternalAnnotator.State, LinterExternalAnnotator.Results>() {

    data class State(
        val linter: LinterConfig,
        val psiWithDocument: Pair<PsiFile, Document>,
        val dialect: String,
        val executeWhenSave: Boolean,
        val config: LinterConfigFile?
    )

    data class Results(val issues: List<Problem>)

    private val log = Logger.getInstance(
        LinterExternalAnnotator::class.java
    )

    override fun collectInformation(file: PsiFile): State? {
        val vfile = file.virtualFile

        if (vfile == null) {
            log.info("Missing vfile for $file")
            return null
        }
        // collect the document here because doAnnotate has no read access to the file document manager
        val document = FileDocumentManager.getInstance().getDocument(vfile)

        if (document == null) {
            log.info("Missing document")
            return null
        }

        val toolchainSettings = file.project.service<LinterExecutionService>()

        val linterType = toolchainSettings.linter

        val configFile = linterType.configPsiFile(file.project, toolchainSettings.configLocation)
        val dialect = configFile?.getDialect() ?: DEFAULT_DIALECT
        val extension = "." + file.fileType.defaultExtension
        val isSqlFileType = isSqlFileType(configFile, extension)

        if (!isSqlFileType) {
            log.info("Invalid sql file type")
            return null
        }

        return State(
            linterType,
            Pair(file, document),
            dialect,
            toolchainSettings.executeWhenSave,
            configFile
        )
    }

    override fun doAnnotate(collectedInfo: State?): Results {
        if (collectedInfo == null) {
            return NO_PROBLEMS_FOUND
        }

        log.info("running sqlfluff Linter external annotator for $collectedInfo")

        val project = collectedInfo.psiWithDocument.first.project

        val settings = project.service<LinterExecutionService>()
        val statusService = project.service<LinterStatusService>()

        if (!toolchainSettings.toolchain().isValid()) {
            log.debug("Scan failed: sqlfluff not available.")
            return NO_PROBLEMS_FOUND
        }

        val linterConfigFile = collectedInfo.config

        if (linterConfigFile == null) {
            log.debug("Scan failed: sqlfluff config file not available.")
            return NO_PROBLEMS_FOUND
        }

        val linterType = collectedInfo.linter
        val executor = linterType.chooseExecutor(project, collectedInfo.executeWhenSave)

        try {
            return CompletableFuture.runAsync {
                statusService.setRunning(true)
            }.thenApplyAsync {
                val request = LintExecutorRequest(
                    collectedInfo,
                    linterType.workDirectory(project, settings.configLocation),
                    linterType.configPath(settings.configLocation),
                    toolchainSettings.toolchain(),
                    PsiFinderFlavor.getApplicableFlavor(),
                    QuickFixFlavor.getApplicableFlavor()
                )
                executor.lint(request)
            }.whenCompleteAsync { _, throwable ->
                if (throwable != null) {
                    log.warn("sqlfluff lint execution failed", throwable)
                }
                statusService.setRunning(false)
            }.join()
        } catch (ex: InterruptedException) {
            return NO_PROBLEMS_FOUND
        } catch (ex: CancellationException) {
            return NO_PROBLEMS_FOUND
        } catch (ex: CompletionException) {
            return NO_PROBLEMS_FOUND
        } catch (ex: LinterException) {
            log.warn("An error occurred while scanning a file.", ex)
            return scanFailedWithError(ex)
        } catch (ex: Throwable) {
            log.warn("An error occurred while scanning a file.", ex)
            return scanFailedWithError(LinterException("An error occurred while scanning a file.", ex))
        }
    }

    private fun scanFailedWithError(e: LinterException): Results {
        LinterErrorNotification(e.message.orEmpty())
            .withTitle("$SQL_FLUFF:")
            .show()

        return NO_PROBLEMS_FOUND
    }

    override fun apply(file: PsiFile, annotationResult: Results?, holder: AnnotationHolder) {
        if (annotationResult == null || !file.isValid) {
            return
        }

        for (problem in annotationResult.issues) {
            log.debug(problem.getMessage())
            problem.createAnnotation(holder)
        }
    }
}

val NO_PROBLEMS_FOUND: LinterExternalAnnotator.Results = LinterExternalAnnotator.Results(emptyList())
