package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.LinterConfigLanguage.LANGUAGE_DEMO_TEXT
import co.anbora.labs.sqlfluff.file.LinterFileType
import co.anbora.labs.sqlfluff.ide.toolchain.LinterKnownToolchainsState
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import co.anbora.labs.sqlfluff.ide.ui.ExecuteWhenView
import co.anbora.labs.sqlfluff.ide.ui.GlobalConfigView
import co.anbora.labs.sqlfluff.ide.ui.PropertyTable
import co.anbora.labs.sqlfluff.ide.utils.toPath
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.io.StreamUtil
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import com.intellij.util.ui.UIUtil
import java.nio.file.Path
import java.util.function.Consumer
import javax.swing.BorderFactory
import kotlin.io.path.exists
import kotlin.io.path.readText

class LinterProjectSettingsForm(private val project: Project?, private val model: Model) {

    data class Model(
        var homeLocation: String,
        var linter: LinterConfig,
        var configPath: String,
        var executeWhenSave: Boolean
    )

    private val linterConfigFile = convertTextToPsi(LANGUAGE_DEMO_TEXT)

    private val globalConfigView = GlobalConfigView(disabledBehavior())
    private val executeView = ExecuteWhenView()

    private val linterConfigPathField = LinterToolchainPathChoosingComboBox(
        FileChooserDescriptorFactory.createSingleFileDescriptor()
    ) { onToolchainLocationChanged() }

    private val linterOptions = PropertyTable()

    private val toolchainChooser = ToolchainChooserComponent({ showNewToolchainDialog() }) { onSelect(it) }

    init {
        createUI()
    }

    private fun createUI() {
        // setup initial location
        model.homeLocation = toolchainChooser.selectedToolchain()?.location ?: ""

        linterConfigPathField.addToolchainsAsync {
            listOf(toolchainSettings.configLocation.toPath())
        }
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
        model.configPath = linterConfigPathField.selectedPath ?: ""
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

    private fun disabledBehavior(): Consumer<LinterConfig> = Consumer {
        model.linter = it
        linterOptions.disableWidget()
        linterConfigPathField.isEnabled = LinterConfig.CUSTOM == it

        when (it) {
            LinterConfig.DISABLED -> Unit
            LinterConfig.GLOBAL -> {
                linterOptions.setProperties(linterConfigFile.getProperties())
            }
            LinterConfig.CUSTOM -> {
                val path = model.configPath.toPath()
                if (path.exists()) {
                    val text = model.configPath.toPath().readText()
                    StreamUtil.convertSeparators(text)
                    val file = convertTextToPsi(text)
                    linterOptions.setProperties(file.getProperties())
                }
            }
        }
    }

    private fun convertTextToPsi(text: String) = PsiFileFactory.getInstance(project)
        .createFileFromText(
            ".sqlfluff",
            LinterFileType,
            text
        ) as LinterConfigFile

    fun createComponent(): DialogPanel {

        val lintFieldsWrapperBuilder = FormBuilder.createFormBuilder()
            .setHorizontalGap(UIUtil.DEFAULT_HGAP)
            .setVerticalGap(UIUtil.DEFAULT_VGAP)

        lintFieldsWrapperBuilder
            .addLabeledComponent("Config .sqlfluff path:", linterConfigPathField)


        val builder = FormBuilder.createFormBuilder()
            .setHorizontalGap(UIUtil.DEFAULT_HGAP)
            .setVerticalGap(UIUtil.DEFAULT_VGAP)

        val panel = builder
            .addComponent(toolchainChooser)
            .addComponent(globalConfigView.getComponent())
            .addComponent(lintFieldsWrapperBuilder.panel)
            .addComponent(linterOptions)
            .addSeparator(4)
            .addComponent(executeView.getComponent())
            .addVerticalGap(4)
            .panel

        val centerPanel = SwingHelper.wrapWithHorizontalStretch(panel)
        centerPanel.border = BorderFactory.createEmptyBorder(5, 0, 0, 0)

        return panel {
            row {
                cell(centerPanel)
                    .align(AlignX.FILL)
            }
        }
    }

    fun reset() {
        toolchainChooser.select(model.homeLocation)
        globalConfigView.reset()
    }
}
