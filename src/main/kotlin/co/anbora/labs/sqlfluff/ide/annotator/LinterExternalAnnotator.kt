package co.anbora.labs.sqlfluff.ide.annotator

import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile.Companion.DEFAULT_DIALECT
import co.anbora.labs.sqlfluff.lint.checker.Problem
import co.anbora.labs.sqlfluff.lint.checker.ScanFiles
import co.anbora.labs.sqlfluff.lint.isSqlFileType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

class LinterExternalAnnotator: ExternalAnnotator<LinterExternalAnnotator.State, LinterExternalAnnotator.Results>() {

    data class State(
        val psiFile: PsiFile,
        val dialect: String,
        val config: LinterConfigFile?
    )

    data class Results(val issues: List<Problem>)

    data class Error(val message: String, val range: TextRange, val severity: HighlightSeverity)

    private val log = Logger.getInstance(
        LinterExternalAnnotator::class.java
    )

    private val linterType = toolchainSettings.linter

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

        val configFile = linterType.configPsiFile(file.project, toolchainSettings.configLocation)
        val dialect = configFile?.getDialect() ?: DEFAULT_DIALECT
        val extension = "." + file.fileType.defaultExtension
        val isSqlFileType = isSqlFileType(configFile, extension)

        if (!isSqlFileType) {
            log.info("Invalid sql file type")
            return null
        }

        return State(file, dialect, configFile)
    }

    override fun doAnnotate(collectedInfo: State?): Results {
        val startTime = System.currentTimeMillis()

        if (collectedInfo == null) {
            return NO_PROBLEMS_FOUND
        }

        log.info("running sqlfluff Linter external annotator for $collectedInfo")

        if (!toolchainSettings.toolchain().isValid()) {
            log.debug("Scan failed: sqlfluff not available.")
            return NO_PROBLEMS_FOUND
        }

        val linterConfigFile = collectedInfo.config

        if (linterConfigFile == null) {
            log.debug("Scan failed: sqlfluff config file not available.")
            return NO_PROBLEMS_FOUND
        }

        val project = collectedInfo.psiFile.project
        val virtualFile = collectedInfo.psiFile.virtualFile
        val psiFile = collectedInfo.psiFile

        val scanFiles = ScanFiles(
            project,
            toolchainSettings.configLocation,
            toolchainSettings.toolchain(),
            listOf(virtualFile)
        )

        val map = scanFiles.call()

        if (map.isEmpty()) {
            return NO_PROBLEMS_FOUND
        }

        val duration: Long = System.currentTimeMillis() - startTime
        log.debug("Sqfluff scan completed: " + psiFile.name + " in " + duration + " ms")
        return Results(map[psiFile] ?: emptyList())
    }

    override fun apply(file: PsiFile, annotationResult: Results?, holder: AnnotationHolder) {
        if (annotationResult == null || !file.isValid) {
            return
        }
    }
}

private val NO_PROBLEMS_FOUND: LinterExternalAnnotator.Results = LinterExternalAnnotator.Results(emptyList())