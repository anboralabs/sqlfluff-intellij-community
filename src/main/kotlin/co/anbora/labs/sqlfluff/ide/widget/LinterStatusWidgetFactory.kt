package co.anbora.labs.sqlfluff.ide.widget

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class LinterStatusWidgetFactory : StatusBarWidgetFactory {
    override fun getId(): String = "sqlfluff.linter.status.widget.factory"

    override fun getDisplayName(): String = "SQLFluff Linter Status"

    override fun isAvailable(project: Project): Boolean = project.getService(LinterStatusSettings::class.java).showWidget

    override fun createWidget(project: Project): StatusBarWidget = LinterStatusWidget(project)

    override fun disposeWidget(widget: StatusBarWidget) {
        widget.dispose()
    }

    override fun canBeEnabledOn(statusBar: com.intellij.openapi.wm.StatusBar): Boolean = true
}
