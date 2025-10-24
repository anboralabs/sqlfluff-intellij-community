package co.anbora.labs.sqlfluff.ide.listener

import co.anbora.labs.sqlfluff.file.LinterFileType
import co.anbora.labs.sqlfluff.ide.actions.LoadConfigFile
import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import co.anbora.labs.sqlfluff.ide.startup.InitConfigFiles
import co.anbora.labs.sqlfluff.ide.toolchain.LinterExecutionService
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import co.anbora.labs.sqlfluff.ide.utils.toPathOrNull
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.openapi.vfs.VirtualFileManager
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

class LinterConfigProjectListener: ProjectActivity {
    override suspend fun execute(project: Project) {
        val configFilePath = LinterFileType.EXTENSION
        val configFile = project.basePath?.toNioPathOrNull()?.resolve(configFilePath)

        val listenerService = project.service<LinterListenerService>()

        VirtualFileManager.getInstance().addAsyncFileListener(ConfigFileVfsListener(project), listenerService)

        val toolchainExecutionSettings = project.service<LinterExecutionService>()

        val previousConfigFile = toolchainExecutionSettings.configLocation.toPathOrNull()

        if (toolchainSettings.toolchain().isValid()) {
            if (configFile != null && configFile.exists()
                && isConfigFileCreated(toolchainExecutionSettings, configFile)) {
                val notification = LinterNotifications.createNotification(
                    "Sqlfluff Linter",
                    "Detected .sqlfluff config file",
                    NotificationType.INFORMATION,
                    LoadConfigFile(project, configFile)
                )
                LinterNotifications.showNotification(notification, project)
            } else if (shouldUseGlobalConfiguration(toolchainExecutionSettings, previousConfigFile)) {
                val notification = LinterNotifications.createNotification(
                    "Sqlfluff Linter",
                    "Please setup default .sqlfluff config file",
                    NotificationType.INFORMATION,
                    LoadConfigFile(project, InitConfigFiles.DEFAULT_CONFIG_PATH, LinterConfig.GLOBAL)
                )
                LinterNotifications.showNotification(notification, project)
            }

            if (configFile != null && isNewConfigFileDetected(toolchainExecutionSettings, configFile)) {
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

    private fun isNewConfigFileDetected(
        toolchainSettings: LinterExecutionService,
        configFile: Path?
    ): Boolean =
        toolchainSettings.linter == LinterConfig.GLOBAL && configFile != null && configFile.exists() && toolchainSettings.configLocation != configFile.pathString

    private fun shouldUseGlobalConfiguration(
        toolchainSettings: LinterExecutionService,
        previousConfigFile: Path?
    ): Boolean = toolchainSettings.linter != LinterConfig.DISABLED
            && InitConfigFiles.DEFAULT_CONFIG_PATH != previousConfigFile

    private fun isConfigFileCreated(
        toolchainSettings: LinterExecutionService,
        configFile: Path
    ): Boolean = (toolchainSettings.linter == LinterConfig.CUSTOM
            && toolchainSettings.configLocation != configFile.pathString)

}
