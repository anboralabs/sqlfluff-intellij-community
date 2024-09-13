package co.anbora.labs.sqlfluff.ide.listener

import co.anbora.labs.sqlfluff.file.LinterFileType.EXTENSION
import co.anbora.labs.sqlfluff.ide.actions.LoadConfigFile
import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.openapi.vfs.AsyncFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent
import com.intellij.openapi.vfs.newvfs.events.VFileEvent


class ConfigFileVfsListener(val project: Project): AsyncFileListener {
    override fun prepareChange(events: MutableList<out VFileEvent>): AsyncFileListener.ChangeApplier? {
        val notifications = events
            .asSequence()
            .filterIsInstance<VFileCreateEvent>()
            .filter { it.childName == EXTENSION }
            .filter { it.parent.path == project.basePath }
            .mapNotNull { it.path.toNioPathOrNull() }
            .map {
                LinterNotifications.createNotification(
                    "Sqlfluff Linter",
                    "Detected .sqlfluff config file",
                    NotificationType.INFORMATION,
                    LoadConfigFile(project, it )
                )
            }
            .toList()

        if (notifications.isEmpty()) {
            return null
        }

        return object : AsyncFileListener.ChangeApplier {
            override fun afterVfsChange() {
                for (notification in notifications) {
                    LinterNotifications.showNotification(notification, project)
                }
                super.afterVfsChange()
            }
        }
    }
}
