package co.anbora.labs.sqlfluff.ide.widget

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level
import com.intellij.openapi.project.Project
import com.intellij.util.messages.Topic

interface LinterStatusListener {
    fun runningChanged(running: Boolean)
}

@Service(Level.PROJECT)
class LinterStatusService(private val project: Project) {

    companion object {
        val TOPIC: Topic<LinterStatusListener> = Topic.create("SqlfluffLinterStatus", LinterStatusListener::class.java)
    }

    @Volatile
    private var _running: Boolean = false

    fun isRunning(): Boolean = _running

    fun setRunning(value: Boolean) {
        if (_running == value) return
        _running = value
        project.messageBus.syncPublisher(TOPIC).runningChanged(value)
    }
}
