package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.ide.startup.InitConfigFiles.Companion.DEFAULT_CONFIG_PATH
import co.anbora.labs.sqlfluff.ide.toolchain.LinterKnownToolchainsState
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import co.anbora.labs.sqlfluff.ide.ui.ExecuteWhenView
import co.anbora.labs.sqlfluff.ide.ui.GlobalConfigView
import co.anbora.labs.sqlfluff.ide.ui.PropertyTable
import co.anbora.labs.sqlfluff.ide.utils.pathAsPath
import co.anbora.labs.sqlfluff.ide.utils.toPath
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile.Companion.DBT_TEMPLATER
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Condition
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import java.nio.file.Path
import java.util.function.Consumer
import javax.swing.BorderFactory
import kotlin.io.path.absolutePathString

class LinterProjectSettingsForm(private val project: Project?, private val model: Model) {

    data class Model(
        var homeLocation: String,
        var linter: LinterConfig,
        var configPath: String,
        var executeWhenSave: Boolean
    )

    private val globalConfigView = GlobalConfigView(enableLinterBehavior())
    private val executeView = ExecuteWhenView(executeWhenBehavior())

    private val linterConfigPathField = LinterToolchainPathChoosingComboBox(
        { getFileSelector() },
        { onToolchainLocationChanged() }
    )

    private val linterOptions = PropertyTable()

    private val toolchainChooser = ToolchainChooserComponent({ showNewToolchainDialog() }) { onSelect(it) }

    init {
        createUI()
    }

    private fun getFileSelector() {
        FileChooser.chooseFile(FileChooserDescriptorFactory.singleFile(), null, null) { file ->
            linterConfigPathField.select(file.pathAsPath)
        }
    }

    private fun createUI() {
        // setup initial location
        model.homeLocation = toolchainChooser.selectedToolchain()?.location ?: ""

        linterConfigPathField.addToolchainsAsync {
            listOf(toolchainSettings.configLocation.toPath())
        }
        linterOptions.disableWidget()
    }

    private fun showNewToolchainDialog() {
        val dialog = LinterNewToolchainDialog(createFilterKnownToolchains(), project)
        if (!dialog.showAndGet()) {
            return
        }

        toolchainChooser.refresh()

        val addedToolchain = dialog.addedToolchain()
        if (addedToolchain != null) {
            toolchainChooser.select(addedToolchain)
        }
    }

    private fun onToolchainLocationChanged() {
        model.configPath = linterConfigPathField.selected()?.toString() ?: ""
    }

    private fun createFilterKnownToolchains(): Condition<Path> {
        val knownToolchains = LinterKnownToolchainsState.getInstance().knownToolchains
        return Condition { path ->
            knownToolchains.none { it == path.toAbsolutePath().toString() }
        }
    }

    private fun onSelect(toolchainInfo: ToolchainInfo) {
        model.homeLocation = toolchainInfo.location
    }

    private fun enableLinterBehavior(): Consumer<LinterConfig> = Consumer {
        model.linter = it
        linterConfigPathField.isEnabled = LinterConfig.CUSTOM == it

        when (it) {
            LinterConfig.DISABLED -> {
                linterConfigPathField.select(null)
                model.configPath = ""
                linterOptions.setProperties(emptyMap())
            }
            LinterConfig.GLOBAL -> {
                linterConfigPathField.select(DEFAULT_CONFIG_PATH)
                model.configPath = DEFAULT_CONFIG_PATH.absolutePathString()
                loadConfigFile(it, DEFAULT_CONFIG_PATH.toString())
            }
            else -> {
                loadConfigFile(it, model.configPath)
            }
        }
    }

    private fun loadConfigFile(config: LinterConfig, configFilePath: String) {
        val linterConfigFile = config.configPsiFile(project, configFilePath)
        if (linterConfigFile != null) {
            linterOptions.setProperties(linterConfigFile.getProperties())
            callbackConfigFile(linterConfigFile)
        }
    }

    private fun executeWhenBehavior(): Consumer<Boolean> = Consumer {
        model.executeWhenSave = it
    }

    private fun callbackConfigFile(configFile: LinterConfigFile) {
        val templater = configFile.getTemplater()
        val isDbt = templater.contains(DBT_TEMPLATER)
        if (isDbt) {
            executeView.selectExecution(true)
        }
        executeView.setEnable(!isDbt)
    }

    fun createComponent(): DialogPanel {
        return panel {
            row {
                cell(toolchainChooser)
                    .align(AlignX.FILL)
            }
            row {
                cell(globalConfigView.getComponent())
                    .align(AlignX.FILL)
            }
            row(".sqlfluff path:") {
                cell(linterConfigPathField)
                    .align(AlignX.FILL)
            }
            row {
                cell(linterOptions)
                    .align(AlignX.FILL)
            }
            separator()
            row {
                cell(executeView.getComponent())
                    .align(AlignX.FILL)
            }
        }.withBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0))
    }

    fun reset() {
        toolchainChooser.select(model.homeLocation)
        globalConfigView.reset()
        executeView.reset()
    }
}
