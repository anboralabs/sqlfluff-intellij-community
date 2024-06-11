package co.anbora.labs.sqlfluff.ide.listener

import co.anbora.labs.sqlfluff.file.LinterFileType
import co.anbora.labs.sqlfluff.ide.actions.LoadConfigFile
import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class LinterConfigProjectListener: ProjectActivity {
    override suspend fun execute(project: Project) {
        val configFilePath = LinterFileType.EXTENSION
        val configFile = project.baseDir.findFileByRelativePath(configFilePath)

        val toolchainSettings = LinterToolchainService.toolchainSettings

        if (configFile != null && !configFile.isDirectory) {

            if (toolchainSettings.linter == LinterConfig.GLOBAL
                || (toolchainSettings.linter == LinterConfig.CUSTOM
                && toolchainSettings.configLocation != configFile.path)) {
                val notification = LinterNotifications.createNotification(
                    "Sqlfluff Linter",
                    "Detected .sqlfluff config file",
                    NotificationType.INFORMATION,
                    LoadConfigFile(project, configFile)
                )
                LinterNotifications.showNotification(notification, project)
            }
        }
    }
}