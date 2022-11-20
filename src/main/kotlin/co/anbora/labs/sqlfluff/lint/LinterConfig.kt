package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile

enum class LinterConfig(protected val linter: Linter) {

    DISABLED(DisabledLinter) {
        override fun lint(
            file: PsiFile,
            document: Document
        ): List<LinterExternalAnnotator.Error> = linter.lint(file, document)
    },
    GLOBAL(GlobalLinter) {
        override fun lint(
            file: PsiFile,
            document: Document
        ): List<LinterExternalAnnotator.Error> = linter.lint(file, document)
    },
    CUSTOM(CustomLinter) {
        override fun lint(
            file: PsiFile,
            document: Document
        ): List<LinterExternalAnnotator.Error> = linter.lint(file, document)
    };

    abstract fun lint(
        file: PsiFile,
        document: Document
    ): List<LinterExternalAnnotator.Error>

}