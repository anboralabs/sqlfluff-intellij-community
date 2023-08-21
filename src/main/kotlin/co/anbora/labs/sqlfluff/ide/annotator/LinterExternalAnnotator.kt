package co.anbora.labs.sqlfluff.ide.annotator

import co.anbora.labs.sqlfluff.ide.settings.Settings
import co.anbora.labs.sqlfluff.lint.LinterConfig
import co.anbora.labs.sqlfluff.lint.isSqlFileType
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import java.util.*

class LinterExternalAnnotator: ExternalAnnotator<LinterExternalAnnotator.CollectedInfo, Collection<LinterExternalAnnotator.Error>>() {

    data class CollectedInfo(val document: Document, val file: PsiFile)
    data class Error(val message: String, val range: TextRange, val severity: HighlightSeverity)

    private val log = Logger.getInstance(
        LinterExternalAnnotator::class.java
    )

    override fun collectInformation(file: PsiFile): CollectedInfo? {
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

        return CollectedInfo(document, file)
    }

    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): CollectedInfo? {
        if (!file.isSqlFileType()) {
            return null
        }

        return collectInformation(file)
    }

    override fun doAnnotate(collectedInfo: CollectedInfo?): Collection<Error> {
        val file = collectedInfo?.file
        if (file == null || !file.isSqlFileType()) {
            return Collections.emptyList()
        }

        log.info("running sqlfluff Linter external annotator for $collectedInfo")

        val linterType = LinterConfig.getOrDefault(Settings[Settings.SELECTED_LINTER])

        return linterType.lint(file, collectedInfo.document)
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
