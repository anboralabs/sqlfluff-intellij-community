package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile

object CustomLinter: Linter() {
    override fun lint(file: PsiFile, document: Document): List<LinterExternalAnnotator.Error> {
        TODO("Not yet implemented")
    }

    override fun buildCommandLineArgs(python: String, lint: String, lintOptions: String, file: PsiFile): SqlFluffLintRunner.Param {
        TODO("Not yet implemented")
    }
}