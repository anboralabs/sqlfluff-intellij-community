package co.anbora.labs.sqlfluff.ide.annotator

import co.anbora.labs.sqlfluff.ide.fs.LinterVirtualFile
import co.anbora.labs.sqlfluff.ide.fs.LinterVirtualFileImpl
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile.Companion.DEFAULT_DIALECT
import co.anbora.labs.sqlfluff.lint.isSqlFileType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import java.util.*

class LinterExternalAnnotator: ExternalAnnotator<LinterVirtualFile, Collection<LinterExternalAnnotator.Error>>() {

    data class Error(val message: String, val range: TextRange, val severity: HighlightSeverity)

    private val log = Logger.getInstance(
        LinterExternalAnnotator::class.java
    )

    private val linterType = toolchainSettings.linter

    override fun collectInformation(file: PsiFile): LinterVirtualFile? {
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

        return LinterVirtualFileImpl(document, file, dialect, isSqlFileType)
    }

    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): LinterVirtualFile? {
        val settings = toolchainSettings
        val linter = settings.linter
        val configFile = linter.configPsiFile(file.project, settings.configLocation)
        val extension = "." + file.fileType.defaultExtension

        if (!isSqlFileType(configFile, extension)) {
            return null
        }

        return collectInformation(file)
    }

    override fun doAnnotate(collectedInfo: LinterVirtualFile?): Collection<Error> {
        val isValid = collectedInfo?.isSqlFileType() ?: false

        if (!isValid) {
            return Collections.emptyList()
        }

        log.info("running sqlfluff Linter external annotator for $collectedInfo")

        return when (collectedInfo) {
            is LinterVirtualFile -> linterType.lint(collectedInfo)
            else -> Collections.emptyList()
        }
    }

    override fun apply(file: PsiFile, annotationResult: Collection<Error>?, holder: AnnotationHolder) {
        annotationResult?.let {
            for (error in it) {
                holder
                    .newAnnotation(error.severity, error.message)
                    .range(error.range)
                    .create()
            }
        }
    }
}
