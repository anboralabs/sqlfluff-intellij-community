package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile

object CustomLinter: Linter() {

    override fun buildCommandLineArgs(
        python: String,
        lint: String,
        lintOptions: String,
        file: PsiFile,
        document: Document
    ): SqlFluffLintRunner.Param {
        TODO("Not yet implemented")
    }
}