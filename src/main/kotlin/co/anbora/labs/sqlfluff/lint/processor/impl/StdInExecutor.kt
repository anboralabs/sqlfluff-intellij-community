package co.anbora.labs.sqlfluff.lint.processor.impl

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.lint.api.ILinterRunner
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.toNioPathOrNull

@Service(Service.Level.PROJECT)
class StdInExecutor(val project: Project) : ILinterRunner() {

    override fun addFileParameters(commandLine: GeneralCommandLine, filePath: String) {
        commandLine.addParameter("-")
        commandLine.addParameter("--stdin-filename")
        commandLine.addParameter(filePath)
    }

    override fun getFilePath(state: LinterExternalAnnotator.State): String {
        val virtualFile = state.psiWithDocument.first.virtualFile
        return virtualFile.toNioPathOrNull()?.toAbsolutePath()?.normalize()?.toString() ?: return virtualFile.path
    }

    override fun getStdinText(state: LinterExternalAnnotator.State): String {
        return state.psiWithDocument.second.text
    }
}
