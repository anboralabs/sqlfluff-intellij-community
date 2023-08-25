package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import java.nio.file.Files
import kotlin.io.path.extension
import kotlin.io.path.pathString

object CustomLinter: Linter() {

    override fun buildCommandLineArgs(
        python: String,
        lint: String,
        lintOptions: String,
        file: PsiFile,
        document: Document
    ): SqlFluffLintRunner.Param {
        return SqlFluffLintRunner.Param(
            execPath = python,
            extraArgs = listOf(lint, LINT_COMMAND, file.virtualFile.path, *lintOptions.split(" ").toTypedArray())
        )
    }
}
