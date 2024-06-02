package co.anbora.labs.sqlfluff.ide.listener

import co.anbora.labs.sqlfluff.file.LinterFileType
import co.anbora.labs.sqlfluff.ide.actions.LoadConfigFile
import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity


class LinterConfigProjectListener: ProjectActivity {
    override suspend fun execute(project: Project) {
        val configFilePath = LinterFileType.EXTENSION
        val configFile = project.baseDir.findFileByRelativePath(configFilePath)

        if (configFile != null && !configFile.isDirectory) {
            val notification = LinterNotifications.createNotification(
                "Sqlfluff Linter",
                "Detected .sqlfluff config file",
                NotificationType.INFORMATION,
                LoadConfigFile(configFile)
            )
            LinterNotifications.showNotification(notification, project)
        }
    }
}