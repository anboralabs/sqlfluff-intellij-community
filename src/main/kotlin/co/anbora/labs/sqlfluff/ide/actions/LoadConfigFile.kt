package co.anbora.labs.sqlfluff.ide.actions

import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.notification.Notification
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import java.nio.file.Path
import kotlin.io.path.pathString

class LoadConfigFile(
    private val project: Project,
    private val configFile: Path,
    private val linterConfig: LinterConfig = LinterConfig.CUSTOM,
): NotificationAction("Load") {

    override fun actionPerformed(
        e: AnActionEvent,
        notification: Notification
    ) {
        val toolchainSettings = LinterToolchainService.toolchainSettings
        toolchainSettings.setConfigPath(configFile.pathString)
        toolchainSettings.setLinterSettingOption(
            LinterToolchainService.LinterConfigSettings(
                linterConfig,
                configFile.pathString,
                true
            )
        )

        notification.expire()

        val notification = LinterNotifications.createNotification(
            "Sqlfluff Linter",
            "Loaded .sqlfluff config file",
            NotificationType.INFORMATION,
        )
        LinterNotifications.showNotification(notification, project)
    }
}
