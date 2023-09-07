package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import co.anbora.labs.sqlfluff.ide.fs.LinterVirtualFile
import kotlin.io.path.pathString

object CustomLinter: Linter() {

    override fun buildCommandLineArgs(
        python: String,
        lint: String,
        lintOptions: String,
        virtualFile: LinterVirtualFile
    ): SqlFluffLintRunner.Param {
        return SqlFluffLintRunner.Param(
            execPath = lint,
            extraArgs = listOf(LINT_COMMAND, virtualFile.canonicalPath().pathString, *lintOptions.split(" ").toTypedArray())
        )
    }
}
