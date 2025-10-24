package co.anbora.labs.sqlfluff.ide.annotator

import co.anbora.labs.sqlfluff.ide.lang.psi.PsiFinderFlavor
import co.anbora.labs.sqlfluff.ide.quickFix.QuickFixFlavor
import co.anbora.labs.sqlfluff.ide.toolchain.LinterExecutionService
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile.Companion.DEFAULT_DIALECT
import co.anbora.labs.sqlfluff.lint.LinterConfig
import co.anbora.labs.sqlfluff.lint.checker.Problem
import co.anbora.labs.sqlfluff.lint.isSqlFileType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.psi.PsiFile
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicReference

class LinterExternalAnnotator: ExternalAnnotator<LinterExternalAnnotator.State, LinterExternalAnnotator.Results>() {

    data class State(
        val linter: LinterConfig,
        val psiWithDocument: Pair<PsiFile, Document>,
        val dialect: String,
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

        return State(linterType, Pair(file, document), dialect, configFile)
    }

    override fun doAnnotate(collectedInfo: State?): Results {
        if (collectedInfo == null) {
            return NO_PROBLEMS_FOUND
        }

        log.info("running sqlfluff Linter external annotator for $collectedInfo")

        val project = collectedInfo.psiWithDocument.first.project

        val settings = project.service<LinterExecutionService>()

        if (!toolchainSettings.toolchain().isValid()) {
            log.debug("Scan failed: sqlfluff not available.")
            return NO_PROBLEMS_FOUND
        }

        val linterConfigFile = collectedInfo.config

        if (linterConfigFile == null) {
            log.debug("Scan failed: sqlfluff config file not available.")
            return NO_PROBLEMS_FOUND
        }

        val result: AtomicReference<Results> = AtomicReference(NO_PROBLEMS_FOUND)
        val latch = CountDownLatch(1)

        ProgressManager.getInstance().run(object : Task.Backgroundable(project, "Running sqlfluff...", false) {
            override fun run(p0: ProgressIndicator) {
                val linterType = collectedInfo.linter

                val linterResult = linterType.lint(
                    collectedInfo,
                    linterType.workDirectory(project, settings.configLocation),
                    settings.configLocation,
                    toolchainSettings.toolchain(),
                    PsiFinderFlavor.getApplicableFlavor(),
                    QuickFixFlavor.getApplicableFlavor()
                )
                result.set(linterResult)

                latch.countDown()
            }
        })

        try {
            latch.await()
        } catch (ex: InterruptedException) {
            return NO_PROBLEMS_FOUND
        }

        return result.get()
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
