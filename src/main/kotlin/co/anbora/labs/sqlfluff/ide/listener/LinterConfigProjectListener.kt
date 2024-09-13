package co.anbora.labs.sqlfluff.ide.listener

import co.anbora.labs.sqlfluff.file.LinterFileType
import co.anbora.labs.sqlfluff.ide.actions.LoadConfigFile
import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService
import co.anbora.labs.sqlfluff.ide.utils.toPathOrNull
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.ide.impl.ProjectUtil
import com.intellij.notification.NotificationType
import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectFileIndex
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.util.io.toNioPathOrNull
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.openapi.vfs.VirtualFileManager
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

class LinterConfigProjectListener: ProjectActivity, Disposable {
    override suspend fun execute(project: Project) {
        val configFilePath = LinterFileType.EXTENSION
        val configFile = project.basePath?.toNioPathOrNull()?.resolve(configFilePath)

        VirtualFileManager.getInstance().addAsyncFileListener(ConfigFileVfsListener(project), this)

        val toolchainSettings = LinterToolchainService.toolchainSettings

        val previousConfigFile = toolchainSettings.configLocation.toPathOrNull()
        val previousConfigFileExists = previousConfigFile?.exists() ?: false

        if (configFile != null && configFile.exists()) {

            if (toolchainSettings.linter == LinterConfig.GLOBAL
                || (toolchainSettings.linter == LinterConfig.CUSTOM
                && toolchainSettings.configLocation != configFile.pathString)) {
                val notification = LinterNotifications.createNotification(
                    "Sqlfluff Linter",
                    "Detected .sqlfluff config file",
                    NotificationType.INFORMATION,
                    LoadConfigFile(project, configFile)
                )
                LinterNotifications.showNotification(notification, project)
            }
        } else if (previousConfigFileExists && previousConfigFile != null) {
            val notification = LinterNotifications.createNotification(
                "Sqlfluff Linter",
                "Please setup default .sqlfluff config file",
                NotificationType.INFORMATION,
                LoadConfigFile(project, previousConfigFile)
            )
            LinterNotifications.showNotification(notification, project)
        }
    }

    override fun dispose() = Unit
}
