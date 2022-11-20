package co.anbora.labs.sqlfluff.ide.runner

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.diagnostic.Logger
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object SqlFluffLintRunner {

    data class Param(val workDirectory: String = "", val execPath: String = "", val extraArgs: List<String> = listOf())

    private val log: Logger = Logger.getInstance(SqlFluffLintRunner::class.java)

    private val TIME_OUT = TimeUnit.SECONDS.toMillis(120L).toInt()
    private const val FILES_NOT_FOUND = 66

    fun runLint(params: Param): Result {
        val result = Result()
        try {
            val out: ProcessOutput = lint(params)
            result.errorOutput = out.stderr
            try {
                if (out.exitCode != FILES_NOT_FOUND) {
                    result.output = out.stdoutLines
                    result.isOk = true
                }
            } catch (e: Exception) {
                log.error(out.stdout)
                result.errorOutput = out.stdout
            }
        } catch (e: Exception) {
            result.errorOutput = e.toString()
        }
        return result
    }

    class Result {
        var isOk = false
        var output: List<String> = listOf()
        var errorOutput: String? = null
    }

    @Throws(ExecutionException::class)
    fun lint(params: Param): ProcessOutput {
        val commandLine = GeneralCommandLine()
        commandLine
            .withCharset(StandardCharsets.UTF_8)
            .setWorkDirectory(params.workDirectory)
        commandLine.exePath = params.execPath

        params.extraArgs.forEach {
            commandLine.addParameter(it)
        }

        return CommandLineRunner.execute(commandLine, TIME_OUT)
    }

}