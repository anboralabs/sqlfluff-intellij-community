package co.anbora.labs.sqlfluff.ide.actions

import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class LoadConfigFile(
    private val project: Project,
    private val configFile: VirtualFile
): DumbAwareAction("Load") {
    override fun actionPerformed(e: AnActionEvent) {
        val toolchainSettings = LinterToolchainService.toolchainSettings
        toolchainSettings.setConfigPath(configFile.path)
        toolchainSettings.setLinterSettingOption(
            LinterToolchainService.LinterConfigSettings(
                LinterConfig.CUSTOM,
                configFile.path,
                true
            )
        )

        val notification = LinterNotifications.createNotification(
            "Sqlfluff Linter",
            "Loaded .sqlfluff config file",
            NotificationType.INFORMATION,
        )
        LinterNotifications.showNotification(notification, project)
    }
}
