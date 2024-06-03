package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import co.anbora.labs.sqlfluff.ide.fs.LinterVirtualFile
import kotlin.io.path.pathString

object CustomLinter: Linter() {

    override fun buildCommandLineArgs(
        lint: String,
        lintOptions: String,
        virtualFile: LinterVirtualFile
    ): SqlFluffLintRunner.Param {
        return SqlFluffLintRunner.Param(
            execPath = lint,
            extraArgs = listOf(
                LinterCommands.LINT_COMMAND,
                virtualFile.canonicalPath().pathString,
                *lintOptions.split(" ").toTypedArray()
            )
        )
    }
}
