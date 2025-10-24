package co.anbora.labs.sqlfluff.ide.actions

import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import co.anbora.labs.sqlfluff.ide.startup.InitConfigFiles
import co.anbora.labs.sqlfluff.ide.toolchain.LinterExecutionService
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlin.io.path.pathString

class UseDefaultConfig(
    private val project: Project
): NotificationAction("Default") {
    override fun actionPerformed(
        e: AnActionEvent,
        notification: Notification
    ) {
        val toolchainSettings = project.service<LinterExecutionService>()
        toolchainSettings.setConfigPath(InitConfigFiles.DEFAULT_CONFIG_PATH.pathString)
        toolchainSettings.setLinterSettingOption(
            LinterExecutionService.LinterConfigSettings(
                LinterConfig.GLOBAL,
                InitConfigFiles.DEFAULT_CONFIG_PATH.pathString,
                true
            )
        )

        notification.expire()

        val notification = LinterNotifications.createNotification(
            "Sqlfluff Linter",
            "Loaded default configuration",
            NotificationType.INFORMATION,
        )
        LinterNotifications.showNotification(notification, project)
    }
}