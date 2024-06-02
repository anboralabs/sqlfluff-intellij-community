package co.anbora.labs.sqlfluff.ide.startup

import co.anbora.labs.sqlfluff.ide.actions.Setup
import co.anbora.labs.sqlfluff.ide.discovery.LinterDiscoveryFlavor
import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import java.util.logging.Logger

class InitConfigFiles: ProjectActivity {

    private val logger = Logger.getLogger(InitConfigFiles::class.simpleName)

    override suspend fun execute(project: Project) {
        LinterDiscoveryFlavor.getApplicableFlavors().forEach {
            logger.info {
                "linter path: ${it.getPathCandidate()}"
            }
        }

        val toolchainSettings = LinterToolchainService.toolchainSettings
        if (!toolchainSettings.cachedToolchain().isValid()) {
            val notification = LinterNotifications.createNotification(
                "Linter",
                "Please setup sqlfluff",
                NotificationType.INFORMATION,
                Setup()
            )

            LinterNotifications.showNotification(notification, project)
        }
    }
}
