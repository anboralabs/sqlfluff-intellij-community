package co.anbora.labs.sqlfluff.ide.listener

import co.anbora.labs.sqlfluff.file.LinterFileType
import co.anbora.labs.sqlfluff.ide.actions.LoadConfigFile
import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import co.anbora.labs.sqlfluff.ide.startup.InitConfigFiles
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService
import co.anbora.labs.sqlfluff.ide.utils.toPathOrNull
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.ide.impl.ProjectUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

class LinterConfigProjectListener: ProjectActivity {
    override suspend fun execute(project: Project) {
        val configFilePath = LinterFileType.EXTENSION
        val configFile = project.basePath?.toNioPathOrNull()?.resolve(configFilePath)

        val listenerService = project.service<LinterListenerService>()

        VirtualFileManager.getInstance().addAsyncFileListener(ConfigFileVfsListener(project), listenerService)

        val toolchainSettings = LinterToolchainService.toolchainSettings

        val previousConfigFile = toolchainSettings.configLocation.toPathOrNull()

        if (toolchainSettings.toolchain().isValid()) {
            if (configFile != null && configFile.exists()
                && isConfigFileCreated(toolchainSettings, configFile)) {
                val notification = LinterNotifications.createNotification(
                    "Sqlfluff Linter",
                    "Detected .sqlfluff config file",
                    NotificationType.INFORMATION,
                    LoadConfigFile(project, configFile)
                )
                LinterNotifications.showNotification(notification, project)
            } else if (shouldUseGlobalConfiguration(toolchainSettings, previousConfigFile)) {
                val notification = LinterNotifications.createNotification(
                    "Sqlfluff Linter",
                    "Please setup default .sqlfluff config file",
                    NotificationType.INFORMATION,
                    LoadConfigFile(project, InitConfigFiles.DEFAULT_CONFIG_PATH, LinterConfig.GLOBAL)
                )
                LinterNotifications.showNotification(notification, project)
            }
        }
    }

    private fun shouldUseGlobalConfiguration(
        toolchainSettings: LinterToolchainService,
        previousConfigFile: Path?
    ): Boolean = toolchainSettings.linter != LinterConfig.DISABLED
            && InitConfigFiles.DEFAULT_CONFIG_PATH != previousConfigFile

    private fun isConfigFileCreated(
        toolchainSettings: LinterToolchainService,
        configFile: Path
    ): Boolean = (toolchainSettings.linter == LinterConfig.CUSTOM
            && toolchainSettings.configLocation != configFile.pathString)

}
