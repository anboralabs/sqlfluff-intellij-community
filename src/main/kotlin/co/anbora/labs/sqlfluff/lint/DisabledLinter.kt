package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import co.anbora.labs.sqlfluff.ide.fs.LinterVirtualFile

object DisabledLinter: Linter() {

    override fun buildCommandLineArgs(
        lint: String,
        lintOptions: String,
        virtualFile: LinterVirtualFile
    ): SqlFluffLintRunner.Param = SqlFluffLintRunner.Param()
}
