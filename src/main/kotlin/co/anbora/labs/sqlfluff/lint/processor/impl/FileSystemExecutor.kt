package co.anbora.labs.sqlfluff.lint.processor.impl

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.annotator.NO_PROBLEMS_FOUND
import co.anbora.labs.sqlfluff.lint.api.ILinterRunner
import co.anbora.labs.sqlfluff.lint.processor.LintExecutorRequest
import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import java.nio.file.Files

@Service(Service.Level.PROJECT)
class FileSystemExecutor(val project: Project) : ILinterRunner() {

    override fun lint(request: LintExecutorRequest): LinterExternalAnnotator.Results {
        val virtualFile = request.state.psiWithDocument.first.virtualFile
        if (virtualFile == null || !virtualFile.isInLocalFileSystem) {
            return NO_PROBLEMS_FOUND
        }
        val path = virtualFile.toNioPath()
        if (!Files.exists(path)) {
            return NO_PROBLEMS_FOUND
        }
        if (FileDocumentManager.getInstance().isFileModified(virtualFile)) {
            return NO_PROBLEMS_FOUND
        }
        return super.lint(request)
    }

    override fun addFileParameters(commandLine: GeneralCommandLine, filePath: String) {
        commandLine.addParameter(filePath)
    }

    override fun getFilePath(state: LinterExternalAnnotator.State): String {
        return state.psiWithDocument.first.virtualFile.toNioPath().toAbsolutePath().normalize().toString()
    }
}
