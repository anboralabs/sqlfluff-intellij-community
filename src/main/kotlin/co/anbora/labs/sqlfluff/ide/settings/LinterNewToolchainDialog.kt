package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.ide.toolchain.LinterKnownToolchainsState
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainFlavor
import co.anbora.labs.sqlfluff.ide.utils.pathAsPath
import co.anbora.labs.sqlfluff.ide.utils.toPath
import co.anbora.labs.sqlfluff.ide.utils.toPathOrNull
import com.intellij.icons.AllIcons
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.Condition
import com.intellij.openapi.util.Disposer
import com.intellij.ui.JBColor
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.layout.ValidationInfoBuilder
import com.intellij.util.ui.JBDimension
import java.nio.file.Path
import javax.swing.JComponent
import javax.swing.JLabel
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

class LinterNewToolchainDialog(private val toolchainFilter: Condition<Path>, project: Project?) : DialogWrapper(project) {
    data class Model(
        var toolchainLocation: String,
        var toolchainVersion: String,
        var stdlibLocation: String,
    )

    private val model: Model = Model("", "N/A", "")
    private val mainPanel: DialogPanel
    private val toolchainVersion = JLabel()
    private val toolchainIconLabel = JLabel()
    private val pathToToolchainComboBox = LinterToolchainPathChoosingComboBox(
        { getFileSelector() },
        { onToolchainLocationChanged() }
    )

    init {
        title = "New Location"
        setOKButtonText("Add")

        mainPanel = panel {
            row("Location:") {
                cell(pathToToolchainComboBox)
                    .align(AlignX.FILL)
                    .validationOnApply { validateToolchainPath() }
            }
            row("Version:") {
                cell(toolchainVersion)
                    .bind(JLabel::getText, JLabel::setText, model::toolchainVersion.toMutableProperty())
                    .gap(RightGap.SMALL)
                    .apply {
                        component.foreground = JBColor.RED
                    }
                cell(toolchainIconLabel)
            }
            row("Binary:") {
                textField()
                    .align(AlignX.FILL)
                    .bindText(model::stdlibLocation)
                    .enabled(false)
            }
        }

        pathToToolchainComboBox.addToolchainsAsync {
            LinterToolchainFlavor.getApplicableFlavors().flatMap { it.suggestHomePaths() }.distinct().
                    filter { toolchainFilter.value(it) }
        }

        val disposable = Disposer.newDisposable()
        mainPanel.registerValidators(disposable)

        init()
    }

    private fun getFileSelector() {
        FileChooser.chooseFile(FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor(), null, null) { file ->
            pathToToolchainComboBox.select(file.pathAsPath)
        }
    }

    override fun getPreferredFocusedComponent(): JComponent = pathToToolchainComboBox

    override fun createCenterPanel(): JComponent {
        return mainPanel.apply {
            preferredSize = JBDimension(450, height)
        }
    }

    override fun doOKAction() {
        if (LinterKnownToolchainsState.getInstance().isKnown(model.toolchainLocation)) {
            setErrorText("This location is already added")
            return
        }

        LinterKnownToolchainsState.getInstance().add(LinterToolchain.fromPath(model.toolchainLocation))

        super.doOKAction()
    }

    fun addedToolchain(): String? {
        return if (exitCode == OK_EXIT_CODE) model.toolchainLocation else null
    }

    private fun onToolchainLocationChanged() {
        model.toolchainLocation = pathToToolchainComboBox.selected()?.toString() ?: ""

        model.toolchainVersion = LinterConfigurationUtil.guessToolchainVersion(model.toolchainLocation)

        if (model.toolchainVersion != LinterConfigurationUtil.UNDEFINED_VERSION) {
            val path = model.toolchainLocation.toPathOrNull()
            model.stdlibLocation = path?.resolve(LinterConfigurationUtil.STANDARD_V_COMPILER)?.toString() ?: ""
        } else {
            model.stdlibLocation = ""
        }

        mainPanel.reset()

        if (model.toolchainVersion == LinterConfigurationUtil.UNDEFINED_VERSION) {
            toolchainVersion.foreground = JBColor.RED
            toolchainIconLabel.icon = null
        } else {
            toolchainVersion.foreground = JBColor.foreground()
            toolchainIconLabel.icon = AllIcons.General.InspectionsOK
        }
    }

    private fun ValidationInfoBuilder.validateToolchainPath(): ValidationInfo? {
        if (model.toolchainLocation.isEmpty()) {
            return error("Sqlfluff location is required")
        }

        val toolchainPath = model.toolchainLocation.toPath()
        if (!toolchainPath.exists()) {
            return error("Sqlfluff location is invalid, $toolchainPath not exist")
        }

        if (!toolchainPath.isDirectory()) {
            return error("Sqlfluff location must be a directory")
        }

        val version = LinterConfigurationUtil.guessToolchainVersion(model.toolchainLocation)
        if (version == LinterConfigurationUtil.UNDEFINED_VERSION) {
            return error("Location for sqlfluff is invalid, can't get version. Please check that folder contains ${LinterConfigurationUtil.STANDARD_V_COMPILER}")
        }

        return null
    }
}
