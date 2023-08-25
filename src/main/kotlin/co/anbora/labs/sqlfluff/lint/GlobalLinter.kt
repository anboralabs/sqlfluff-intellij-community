package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import java.nio.file.Files
import kotlin.io.path.extension
import kotlin.io.path.pathString

object GlobalLinter: Linter() {

    override fun buildCommandLineArgs(
        python: String,
        lint: String,
        lintOptions: String,
        file: PsiFile,
        document: Document
    ): SqlFluffLintRunner.Param {
        val nioFile = file.virtualFile.toNioPath()
        return SqlFluffLintRunner.Param(
            execPath = SQL_FLUFF,
            extraArgs = listOf(LINT_COMMAND, nioFile.pathString, *lintOptions.split(" ").toTypedArray())
        )
    }
}
