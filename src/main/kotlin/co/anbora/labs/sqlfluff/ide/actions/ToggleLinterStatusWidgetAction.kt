package co.anbora.labs.sqlfluff.ide.actions

import co.anbora.labs.sqlfluff.ide.widget.LinterStatusSettings
import co.anbora.labs.sqlfluff.ide.widget.LinterStatusWidgetFactory
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager
import kotlin.jvm.java

class ToggleLinterStatusWidgetAction : ToggleAction(), DumbAware {
    override fun isSelected(e: AnActionEvent): Boolean {
        val project = e.project ?: return false
        return project.service<LinterStatusSettings>().showWidget
    }

    override fun setSelected(e: AnActionEvent, state: Boolean) {
        val project = e.project ?: return
        val settings = project.service<LinterStatusSettings>()
        settings.showWidget = state

        project.service<StatusBarWidgetsManager>().updateWidget(LinterStatusWidgetFactory::class.java)
    }

    override fun getActionUpdateThread(): ActionUpdateThread {
        return ActionUpdateThread.EDT
    }
}
