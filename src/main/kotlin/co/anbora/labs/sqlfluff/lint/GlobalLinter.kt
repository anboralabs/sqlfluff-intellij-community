package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import com.intellij.psi.PsiFile
import kotlin.io.path.pathString

private const val SQL_FLUFF = "sqlfluff"

object GlobalLinter: Linter() {

    override fun buildCommandLineArgs(
        python: String,
        lint: String,
        lintOptions: String,
        file: PsiFile
    ): SqlFluffLintRunner.Param {
        return SqlFluffLintRunner.Param(
            workDirectory = file.virtualFile.toNioPath().parent.pathString,
            execPath = SQL_FLUFF,
            extraArgs = listOf("lint", file.virtualFile.canonicalPath.orEmpty(), *lintOptions.split(" ").toTypedArray())
        )
    }
}