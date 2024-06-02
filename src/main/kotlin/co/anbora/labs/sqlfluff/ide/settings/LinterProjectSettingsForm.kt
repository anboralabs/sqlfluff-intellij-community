package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.LinterConfigLanguage.LANGUAGE_DEMO_TEXT
import co.anbora.labs.sqlfluff.file.LinterFileType
import co.anbora.labs.sqlfluff.ide.settings.Settings.OPTION_KEY_SQLLINT
import co.anbora.labs.sqlfluff.ide.toolchain.LinterKnownToolchainsState
import co.anbora.labs.sqlfluff.ide.ui.ExecuteWhenView
import co.anbora.labs.sqlfluff.ide.ui.GlobalConfigView
import co.anbora.labs.sqlfluff.ide.ui.PropertyTable
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import co.anbora.labs.sqlfluff.lint.LinterConfig
import co.anbora.labs.sqlfluff.settings.findColorByKey
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextComponentAccessors
import com.intellij.openapi.util.Condition
import com.intellij.psi.PsiFileFactory
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.intellij.util.NotNullProducer
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.nio.file.Path
import java.util.function.Consumer
import java.util.function.Supplier
import javax.swing.BorderFactory
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.plaf.basic.BasicComboBoxEditor

class LinterProjectSettingsForm(private val project: Project?, private val model: Model) {

    val DEFAULT_CONFIG = PsiFileFactory.getInstance(project)
        .createFileFromText(
            "DUMMY.rules",
            LinterFileType,
            LANGUAGE_DEMO_TEXT
        ) as LinterConfigFile

    private val globalConfigView = GlobalConfigView(disabledBehavior())
    private val executeView = ExecuteWhenView()

    private lateinit var linterPathField: TextFieldWithHistoryWithBrowseButton

    private val linterOptions = PropertyTable()

    data class Model(
        var homeLocation: String,
    )

    private val toolchainChooser = ToolchainChooserComponent({ showNewToolchainDialog() }) { onSelect(it) }

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

    private fun createFilterKnownToolchains(): Condition<Path> {
        val knownToolchains = LinterKnownToolchainsState.getInstance().knownToolchains
        return Condition { path ->
            knownToolchains.none { it == path.toAbsolutePath().toString() }
        }
    }

    private fun onSelect(toolchainInfo: ToolchainInfo) {
        model.homeLocation = toolchainInfo.location
    }

    init {
        createUI()
    }

    private fun createUI() {

        linterPathField = createTextFieldWithHistory(detectLinters())
        linterPathField.addBrowseFolderListener(
            "",
            "Path",
            null,
            FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(),
            TextComponentAccessors.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT
        );
        setupTextFieldDefaultValue(linterPathField.childComponent.textEditor) {
            Settings[OPTION_KEY_SQLLINT]
        }

        // setup initial location
        model.homeLocation = toolchainChooser.selectedToolchain()?.location ?: ""
    }

    private fun createTextFieldWithHistory(defaultValues: NotNullProducer<List<String>>): TextFieldWithHistoryWithBrowseButton {
        val textFieldWithHistoryWithBrowseButton = TextFieldWithHistoryWithBrowseButton()
        val textFieldWithHistory = textFieldWithHistoryWithBrowseButton.childComponent
        textFieldWithHistory.editor = object : BasicComboBoxEditor() {
            override fun createEditorComponent(): JTextField {
                return JBTextField()
            }
        }
        textFieldWithHistory.setHistorySize(-1)
        textFieldWithHistory.setMinimumAndPreferredWidth(0)
        SwingHelper.addHistoryOnExpansion(textFieldWithHistory, defaultValues)
        return textFieldWithHistoryWithBrowseButton
    }

    private fun setupTextFieldDefaultValue(
        textField: JTextField,
        defaultValueSupplier: Supplier<String?>
    ) {
        val defaultShellPath: String? = defaultValueSupplier.get()
        if (defaultShellPath.isNullOrBlank()) return
        textField.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                textField.foreground = if (defaultShellPath == textField.text) getDefaultValueColor() else getChangedValueColor()
            }
        })
        if (textField is JBTextField) {
            textField.emptyText.text = defaultShellPath
        }
    }

    fun getDefaultValueColor(): Color? = findColorByKey("TextField.inactiveForeground", "nimbusDisabledText")

    fun getChangedValueColor(): Color? = findColorByKey("TextField.foreground")

    private fun detectLinters(): NotNullProducer<List<String>> = NotNullProducer { arrayListOf() }

    private fun disabledBehavior(): Consumer<LinterConfig> = Consumer {
        linterOptions.disableWidget()
        linterPathField.isEnabled = LinterConfig.CUSTOM == it

        when (it) {
            LinterConfig.DISABLED -> Unit
            LinterConfig.GLOBAL -> {
                linterOptions.setProperties(DEFAULT_CONFIG.getProperties())
            }
            LinterConfig.CUSTOM -> {
                linterOptions.setProperties(emptyMap())
            }
        }
    }

    fun createComponent(): DialogPanel {

        val lintFieldsWrapperBuilder = FormBuilder.createFormBuilder()
            .setHorizontalGap(UIUtil.DEFAULT_HGAP)
            .setVerticalGap(UIUtil.DEFAULT_VGAP)

        lintFieldsWrapperBuilder
            .addLabeledComponent("Config .sqlfluff path:", linterPathField)


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
