package co.anbora.labs.sqlfluff.ide.notifications

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import com.intellij.notification.impl.NotificationFullContent
import com.intellij.openapi.application.invokeLater
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.project.Project

open class LinterNotification(
    content: String = "",
    type: NotificationType = NotificationType.INFORMATION,
): Notification(ID, content, type), NotificationFullContent {

    companion object {
        const val ID = "SqlFluffCommunityNotification"
        private val LOG = logger<LinterNotification>()
    }

    fun withTitle(title: String): LinterNotification {
        setTitle(title)
        return this
    }

    fun show(project: Project? = null) {
        invokeLater {
            Notifications.Bus.notify(this, project)
            LOG.info("Notification: title: $title, content: ${content.ifEmpty { "<empty>" }}, type: $type")
        }
    }
}