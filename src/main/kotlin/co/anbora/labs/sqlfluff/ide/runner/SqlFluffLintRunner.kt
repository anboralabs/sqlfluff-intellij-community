package co.anbora.labs.sqlfluff.ide.runner

import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.ProcessOutput
import com.intellij.openapi.diagnostic.Logger
import java.nio.charset.StandardCharsets
import java.util.concurrent.TimeUnit

object SqlFluffLintRunner {

    data class Param(val execPath: String = "", val extraArgs: List<String> = listOf())

    private val log: Logger = Logger.getInstance(SqlFluffLintRunner::class.java)

    private val TIME_OUT = TimeUnit.SECONDS.toMillis(120L).toInt()
    private const val OK = 0

    fun runLint(projectPath: String?, params: Param): Result {
        val result = Result()
        try {
            val out: ProcessOutput = lint(projectPath, params)
            result.errorOutput = out.stderr
            try {
                if (isOkExecution(out)) {
                    result.output = out.stdoutLines
                    result.isOk = true
                }
            } catch (e: Exception) {
                val output = out.stdout.replace("User Error: ", "")
                result.errorOutput = output
            }
        } catch (e: Exception) {
            result.errorOutput = e.toString()
        }
        return result
    }

    private fun isOkExecution(out: ProcessOutput): Boolean {
        val okResult = out.exitCode == OK
        if (!okResult) {
            throw IllegalArgumentException()
        }
        return okResult
    }

    class Result {
        var isOk = false
        var output: List<String> = listOf()
        var errorOutput: String? = null

        fun hasErrors(): Boolean = errorOutput != null
    }

    @Throws(ExecutionException::class)
    fun lint(projectPath: String?, params: Param): ProcessOutput {
        val commandLine = GeneralCommandLine()
        commandLine
            .withCharset(StandardCharsets.UTF_8)
            .withWorkDirectory(projectPath)
        commandLine.exePath = params.execPath

        params.extraArgs.forEach {
            commandLine.addParameter(it)
        }

        return CommandLineRunner.execute(commandLine, TIME_OUT)
    }

}
