package co.anbora.labs.sqlfluff.ide.listener

import co.anbora.labs.sqlfluff.ide.toolchain.LinterExecutionService
import co.anbora.labs.sqlfluff.lint.LinterConfig
import co.anbora.labs.sqlfluff.lint.isSqlFileType
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.ProjectManager
import com.intellij.psi.PsiManager

class SqlFileSaveListener : FileDocumentManagerListener {

    override fun afterDocumentSaved(document: Document) {
        val virtualFile = FileDocumentManager.getInstance().getFile(document) ?: return

        for (project in ProjectManager.getInstance().openProjects) {
            if (project.isDisposed) continue

            val executionService = project.service<LinterExecutionService>()
            val linterType = executionService.linter

            if (linterType == LinterConfig.DISABLED) continue

            val configFile = linterType.configPsiFile(project, executionService.configLocation) ?: continue
            val extension = virtualFile.extension?.let { ".$it" } ?: continue

            if (!isSqlFileType(configFile, extension)) continue

            val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: continue
            DaemonCodeAnalyzer.getInstance(project).restart(psiFile, "saved document")
        }
    }
}
