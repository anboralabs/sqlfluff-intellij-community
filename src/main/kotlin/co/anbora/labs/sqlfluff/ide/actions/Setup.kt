package co.anbora.labs.sqlfluff.ide.actions

import co.anbora.labs.sqlfluff.ide.SqlfluffBundle
import co.anbora.labs.sqlfluff.ide.settings.LinterProjectSettingsConfigurable
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.ProjectManager

class Setup: DumbAwareAction(SqlfluffBundle.message("action.setup.text")) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: ProjectManager.getInstance().defaultProject
        LinterProjectSettingsConfigurable.show(project)
    }
}
