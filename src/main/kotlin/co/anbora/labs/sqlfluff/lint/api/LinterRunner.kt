package co.anbora.labs.sqlfluff.lint.api

import co.anbora.labs.sqlfluff.ide.runner.CommandLineRunner
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import co.anbora.labs.sqlfluff.lint.LinterCommands
import co.anbora.labs.sqlfluff.lint.exception.LinterException
import co.anbora.labs.sqlfluff.lint.issue.FileIssue
import co.anbora.labs.sqlfluff.lint.issue.Issue
import co.anbora.labs.sqlfluff.lint.issue.IssueMapper
import co.anbora.labs.sqlfluff.lint.issue.LinterIssueMapper
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.project.Project
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object LinterRunner {

    private val TIME_OUT = TimeUnit.SECONDS.toMillis(120L).toInt()
    private const val SUCCESS_NO_ISSUES_FOUND = 0
    private const val SUCCESS_ISSUES_FOUND = 1
    private const val ERROR_OCCURRED = 2

    private val successCode = setOf(SUCCESS_NO_ISSUES_FOUND, SUCCESS_ISSUES_FOUND)


    fun lint(
        project: Project,
        configPath: String,
        toolchain: LinterToolchain,
        filesToScan: Set<String>
    ): List<Issue> {

        if (filesToScan.isEmpty()) {
            throw LinterException("Illegal state: filesToScan is empty")
        }

        if (!toolchain.isValid()) {
            throw LinterException("Path to sqfluff executable not set (check Plugin Settings)")
        }

        if (configPath.isEmpty()) {
            throw LinterException("Path to .sqlfluff config not set (check Plugin Settings)")
        }

        val commandLine = GeneralCommandLine(toolchain.binPath())

        commandLine.charset = StandardCharsets.UTF_8

        commandLine.addParameter(LinterCommands.LINT_COMMAND)

        for (file in filesToScan) {
            commandLine.addParameter(file)
        }

        commandLine.addParameter("--config")
        commandLine.addParameter(configPath)
        commandLine.addParameter("--format")
        commandLine.addParameter("json")

        commandLine.setWorkDirectory(project.basePath)

        val result = this.scan(commandLine)

        return result.map {
            IssueMapper.apply(it)
        }.flatten()
    }

    private fun scan(commandLine: GeneralCommandLine): List<FileIssue> {
        val processOutput = CommandLineRunner.execute(commandLine, TIME_OUT)

        if (isOkExecution(processOutput)) {
            return processOutput.stdoutLines.map {
                LinterIssueMapper.apply(it)
            }.flatten()
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
