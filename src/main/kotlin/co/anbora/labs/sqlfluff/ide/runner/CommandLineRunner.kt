package co.anbora.labs.sqlfluff.ide.runner

import com.google.common.base.Charsets
import com.intellij.execution.ExecutionException
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.execution.process.*
import com.intellij.openapi.util.Key

object CommandLineRunner {

    @Throws(ExecutionException::class)
    fun execute(commandLine: GeneralCommandLine, timeoutInMilliseconds: Int): ProcessOutput {
        val command = commandLine.commandLineString
        val process = commandLine.createProcess()
        val processHandler = OSProcessHandler(process, command, Charsets.UTF_8)
        val output = ProcessOutput()
        processHandler.addProcessListener(object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                if (outputType == ProcessOutputTypes.STDERR) {
                    output.appendStderr(event.text)
                } else if (outputType != ProcessOutputTypes.SYSTEM) {
                    output.appendStdout(event.text)
                }
            }
        })
        processHandler.startNotify()
        if (processHandler.waitFor(timeoutInMilliseconds.toLong())) {
            output.exitCode = processHandler.exitCode!!
        } else {
            processHandler.destroyProcess()
            output.setTimeout()
        }
        if (output.isTimeout) {
            throw ExecutionException("Command '" + commandLine.commandLineString + "' is timed out.")
        }
        return output
    }
}