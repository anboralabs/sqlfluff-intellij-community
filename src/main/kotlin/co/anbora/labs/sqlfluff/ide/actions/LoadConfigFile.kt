package co.anbora.labs.sqlfluff.ide.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.vfs.VirtualFile

class LoadConfigFile(configFile: VirtualFile): DumbAwareAction("Load") {
    override fun actionPerformed(e: AnActionEvent) {

    }
}