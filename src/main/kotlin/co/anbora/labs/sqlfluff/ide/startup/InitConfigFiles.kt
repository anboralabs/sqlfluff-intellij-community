package co.anbora.labs.sqlfluff.ide.startup

import co.anbora.labs.sqlfluff.LinterConfigLanguage.LANGUAGE_DEMO_TEXT
import co.anbora.labs.sqlfluff.ide.actions.Setup
import co.anbora.labs.sqlfluff.ide.discovery.LinterDiscoveryFlavor
import co.anbora.labs.sqlfluff.ide.notifications.LinterNotifications
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Logger
import kotlin.io.path.exists

class InitConfigFiles: ProjectActivity {

    private val logger = Logger.getLogger(InitConfigFiles::class.simpleName)

    override suspend fun execute(project: Project) {
        LinterDiscoveryFlavor.getApplicableFlavors().forEach {
            logger.info {
                "linter path: ${it.getPathCandidate()}"
            }
        }

        checkDefaultConfigFile()

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

    private fun checkDefaultConfigFile() {
        val pathConfig = DEFAULT_CONFIG_PATH
        if (!pathConfig.exists()) {
            val configFile = pathConfig.toFile().createNewFile()
            Files.write(pathConfig, LANGUAGE_DEMO_TEXT.toByteArray())
        }
    }

    companion object {
        val DEFAULT_CONFIG_PATH: Path = PathManager.getConfigDir().resolve(".sqlfluff")
    }
}
