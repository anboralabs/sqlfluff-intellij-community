package co.anbora.labs.sqlfluff.ide.notifications

import co.anbora.labs.sqlfluff.ide.icons.LinterIcons
import com.intellij.notification.Notification
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.project.Project

object LinterNotifications {

    @JvmStatic
    fun createNotification(
        title: String,
        content: String,
        type: NotificationType,
        vararg actions: AnAction
    ): Notification {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup("sql.fluff.notification")
            .createNotification(content, type)
            .setTitle(title)
            .setIcon(LinterIcons.SQL_FLUFF_32)

        for (action in actions) {
            notification.addAction(action)
        }

        return notification
    }

    @JvmStatic
    fun showNotification(notification: Notification, project: Project?) {
        try {
            notification.notify(project)
        } catch (e: Exception) {
            notification.notify(project)
        }
    }
}
