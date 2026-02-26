package co.anbora.labs.sqlfluff.lint.api

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.annotator.NO_PROBLEMS_FOUND
import co.anbora.labs.sqlfluff.ide.runner.CommandLineRunner
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import co.anbora.labs.sqlfluff.lint.LinterCommands
import co.anbora.labs.sqlfluff.lint.exception.LinterException
import co.anbora.labs.sqlfluff.lint.issue.Issue
import co.anbora.labs.sqlfluff.lint.issue.IssueMapper
import co.anbora.labs.sqlfluff.lint.issue.LinterIssueMapper
import co.anbora.labs.sqlfluff.lint.processor.ILintExecutor
import co.anbora.labs.sqlfluff.lint.processor.LintExecutorRequest
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import java.util.concurrent.TimeUnit

data class CommandLineParams(
    val toolchain: LinterToolchain,
    val configPath: String,
    val workDirectory: Path?,
    val filePath: String
)

data class ScanParams(
    val commandLine: GeneralCommandLine,
    val filePath: String,
    val stdinText: String?
)

abstract class ILinterRunner : ILintExecutor {

    companion object {
        private val TIME_OUT = TimeUnit.SECONDS.toMillis(120L).toInt()
        private const val SUCCESS_NO_ISSUES_FOUND = 0
        private const val SUCCESS_ISSUES_FOUND = 1
        private val successCode = setOf(SUCCESS_NO_ISSUES_FOUND, SUCCESS_ISSUES_FOUND)
    }

    override fun lint(request: LintExecutorRequest): LinterExternalAnnotator.Results {
        val toolchain = request.toolchain
        val configPath = request.configPath
        val workDirectory = request.workDirectory
        val state = request.state

        if (!toolchain.isValid()) {
            throw LinterException("Path to sqlfluff executable not set (check Plugin Settings)")
        }

        if (configPath.isEmpty()) {
            throw LinterException("Path to .sqlfluff config not set (check Plugin Settings)")
        }

        val filePath = getFilePath(state)
        val stdinText = getStdinText(state)

        val params = CommandLineParams(toolchain, configPath, workDirectory, filePath)
        val commandLine = buildCommandLine(params)

        val scanParams = ScanParams(commandLine, filePath, stdinText)
        val issues = scan(scanParams)

        val psiFile = state.psiWithDocument.first
        val document = state.psiWithDocument.second

        val filePathsToElements: Map<String, Pair<PsiFile, Document>> = mapOf(
            filePath to Pair(psiFile, document)
        )

        val findThread = ProcessResultsThread(
            request.psiFinder, request.quickFixer,
            issues, filePathsToElements
        )

        ReadAction.run(findThread)

        val problems = findThread.getProblems()
        if (problems.isEmpty()) {
            return NO_PROBLEMS_FOUND
        }

        return LinterExternalAnnotator.Results(problems[psiFile] ?: emptyList())
    }

    protected fun buildCommandLine(params: CommandLineParams): GeneralCommandLine {
        val commandLine = GeneralCommandLine(params.toolchain.binPath())
        commandLine.charset = StandardCharsets.UTF_8
        commandLine.addParameter(LinterCommands.LINT_COMMAND)

        addFileParameters(commandLine, params.filePath)

        commandLine.addParameter("--config")
        commandLine.addParameter(params.configPath)
        commandLine.addParameter("--format")
        commandLine.addParameter("json")
        commandLine.setWorkDirectory(params.workDirectory?.toFile())

        return commandLine
    }

    protected abstract fun addFileParameters(commandLine: GeneralCommandLine, filePath: String)

    protected abstract fun getFilePath(state: LinterExternalAnnotator.State): String

    protected open fun getStdinText(state: LinterExternalAnnotator.State): String? = null

    private fun scan(scanParams: ScanParams): List<Issue> {
        val processOutput = CommandLineRunner.execute(scanParams.commandLine, TIME_OUT, scanParams.stdinText)
        if (isOkExecution(processOutput)) {
            val fileIssues = processOutput.stdoutLines.flatMap {
                LinterIssueMapper.apply(it)
            }
            return fileIssues.flatMap {
                IssueMapper.apply(it, scanParams)
            }
        }
        throw LinterException(processOutput.stdout)
    }

    private fun isOkExecution(out: ProcessOutput): Boolean {
        val okResult = out.exitCode in successCode
        if (!okResult) {
            throw LinterException(out.stderr)
        }
        return true
    }
}
