package co.anbora.labs.sqlfluff.lint.api

import co.anbora.labs.sqlfluff.ide.runner.CommandLineRunner
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import co.anbora.labs.sqlfluff.lint.LinterCommands
import co.anbora.labs.sqlfluff.lint.exception.LinterException
import co.anbora.labs.sqlfluff.lint.issue.IssueItem
import co.anbora.labs.sqlfluff.lint.issue.IssueMapper
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.project.Project
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object LinterRunner {

    private val TIME_OUT = TimeUnit.SECONDS.toMillis(120L).toInt()

    fun scan(
        project: Project,
        configPath: String,
        toolchain: LinterToolchain,
        filesToScan: Set<String>
    ): List<IssueItem> {

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

        val processOutput = CommandLineRunner.execute(commandLine, TIME_OUT)

        return processOutput.stdoutLines.map {
            IssueMapper.apply(it)
        }.flatten()
            .mapNotNull { it.violations }
            .flatten()
    }
}