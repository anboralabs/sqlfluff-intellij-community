package co.anbora.labs.sqlfluff.ide.widget

import co.anbora.labs.sqlfluff.ide.icons.LinterIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.ui.AnimatedIcon
import com.intellij.util.Consumer
import java.awt.event.MouseEvent
import javax.swing.Icon

class LinterStatusWidget(private val project: Project) : StatusBarWidget, StatusBarWidget.Multiframe, StatusBarWidget.IconPresentation {

    companion object {
        const val WIDGET_ID = "sqlfluff.linter.status.widget"
    }

    private var statusBar: StatusBar? = null
    private var running: Boolean = project.getService(LinterStatusService::class.java).isRunning()

    private val connection = project.messageBus.connect()

    init {
        connection.subscribe(LinterStatusService.TOPIC, object : LinterStatusListener {
            override fun runningChanged(running: Boolean) {
                this@LinterStatusWidget.running = running
                statusBar?.updateWidget(ID())
            }
        })
    }

    override fun ID(): String = WIDGET_ID

    override fun getPresentation(): StatusBarWidget.WidgetPresentation = this

    override fun install(statusBar: StatusBar) {
        this.statusBar = statusBar
    }

    override fun dispose() {
        connection.dispose()
    }

    override fun getIcon(): Icon = if (running) AnimatedIcon.Default() else LinterIcons.SQL_FLUFF_16

    override fun getTooltipText(): String? = if (running) "Running..." else "Idle"

    override fun getClickConsumer(): Consumer<MouseEvent> = Consumer { }

    override fun copy(): StatusBarWidget = LinterStatusWidget(project)
}
