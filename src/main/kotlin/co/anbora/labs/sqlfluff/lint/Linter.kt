package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.fs.LinterVirtualFile
import co.anbora.labs.sqlfluff.ide.notifications.LinterErrorNotification
import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService
import co.anbora.labs.sqlfluff.lint.issue.IssueItem
import co.anbora.labs.sqlfluff.lint.issue.IssueMapper
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import java.util.regex.Pattern

const val SQL_FLUFF = "sqlfluff"

sealed class Linter {

    protected val LOGGER: Logger = Logger.getInstance(
        Linter::class.java
    )

    private val DEFAULT_FORMAT = " --format json"

    fun lint(
        virtualFile: LinterVirtualFile
    ): List<LinterExternalAnnotator.Error> {

        // First time users will not have this Option set if they do not open the Settings
        // UI yet.
        val dialectArgument = "--dialect ${virtualFile.dialect()}"

        val args = buildCommandLineArgs(
            LinterToolchainService.toolchainSettings.toolchain().binPath(),
            dialectArgument + DEFAULT_FORMAT,
            virtualFile
        )

        return runLinter(virtualFile, args)
    }

    private fun runLinter(
        virtualFile: LinterVirtualFile,
        args: SqlFluffLintRunner.Param
    ): List<LinterExternalAnnotator.Error> {
        val result = SqlFluffLintRunner.runLint(virtualFile.projectPath(), args)

        if (result.hasErrors() || result.hasSystemErrors()) {
            LinterErrorNotification(result.errorOutput.orEmpty())
                .withTitle("$SQL_FLUFF:")
                .show()
            return emptyList()
        }

        return errors(result, virtualFile.document())
    }

    private fun errors(
        result: SqlFluffLintRunner.Result,
        document: Document
    ): List<LinterExternalAnnotator.Error> {
        return result.output.asSequence().map {
            IssueMapper.apply(it)
        }
            .flatten()
            .mapNotNull { it.violations }
            .flatten()
            .mapNotNull {
                parseLintResult(
                    document,
                    it
                )
            }.toList()
    }

    private val WARNING_PATTERN = Pattern.compile("(\\w+\\d+)")

    private fun parseLintResult(
        document: Document,
        line: IssueItem
    ): LinterExternalAnnotator.Error? {

        var lineNumber = line.lineNo ?: 0
        val lineCount = document.lineCount
        if (0 == lineCount) {
            return null
        }
        lineNumber = if (lineNumber > 0) lineNumber - 1 else lineNumber

        val position = line.linePos ?: 0
        val errorType = line.code.orEmpty()
        val errorDescription = line.description

        val lineStartOffset = document.getLineStartOffset(lineNumber)

        val errorMessage = "$SQL_FLUFF [$errorType]: $errorDescription"

        val initialPosition = if (position > 0) position - 1 else 0

        val lit = TextRange.from(lineStartOffset + initialPosition, 0)

        val severity = when {
            WARNING_PATTERN.matcher(errorType).matches() -> HighlightSeverity.WARNING
            else -> HighlightSeverity.ERROR
        }

        return LinterExternalAnnotator.Error(
            errorMessage,
            lit,
            severity
        )
    }

    abstract fun buildCommandLineArgs(
        lint: String,
        lintOptions: String,
        virtualFile: LinterVirtualFile
    ): SqlFluffLintRunner.Param
}
