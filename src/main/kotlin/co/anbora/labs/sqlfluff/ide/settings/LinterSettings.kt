package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.ide.ui.GlobalConfigView
import co.anbora.labs.sqlfluff.ide.settings.Settings.OPTION_KEY_PYTHON
import co.anbora.labs.sqlfluff.ide.settings.Settings.OPTION_KEY_SQLLINT
import co.anbora.labs.sqlfluff.ide.settings.Settings.OPTION_KEY_SQLLINT_ARGUMENTS
import co.anbora.labs.sqlfluff.lint.LinterConfig
import co.anbora.labs.sqlfluff.settings.findColorByKey
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextComponentAccessors
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.TextFieldWithHistoryWithBrowseButton
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.components.JBTextField
import com.intellij.util.NotNullProducer
import com.intellij.util.ui.FormBuilder
import com.intellij.util.ui.SwingHelper
import com.intellij.util.ui.UIUtil
import java.awt.Color
import java.util.Objects
import java.util.function.Consumer
import java.util.function.Supplier
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.plaf.basic.BasicComboBoxEditor

class LinterSettings: Configurable {

    private lateinit var pythonPathField: TextFieldWithHistoryWithBrowseButton
    private lateinit var linterPathField: TextFieldWithHistoryWithBrowseButton
    private lateinit var argumentsField: JTextField

    private val globalConfigView = GlobalConfigView(disabledBehavior())

    init {
        createUI()
    }

    private fun createUI() {
        pythonPathField = createTextFieldWithHistory(detectPython())
        pythonPathField.addBrowseFolderListener(
            "",
            "Path",
            null,
            FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor(),
            TextComponentAccessors.TEXT_FIELD_WITH_HISTORY_WHOLE_TEXT
        );
        setupTextFieldDefaultValue(pythonPathField.childComponent.textEditor) {
            Settings[OPTION_KEY_PYTHON]
        }

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

        argumentsField = JTextField()
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

    override fun getDisplayName(): String = "Sqlfluff"

    override fun getHelpTopic(): String? = null

    override fun createComponent(): JComponent {

        val lintFieldsWrapperBuilder = FormBuilder.createFormBuilder()
            .setHorizontalGap(UIUtil.DEFAULT_HGAP)
            .setVerticalGap(UIUtil.DEFAULT_VGAP)

        lintFieldsWrapperBuilder.addLabeledComponent("Python path:", pythonPathField)
            .addLabeledComponent("sqlfluff.py path:", linterPathField)
            .addLabeledComponent("Arguments: ", argumentsField)

        val builder = FormBuilder.createFormBuilder()
            .setHorizontalGap(UIUtil.DEFAULT_HGAP)
            .setVerticalGap(UIUtil.DEFAULT_VGAP)

        val panel = builder
            .addComponent(globalConfigView.getComponent())
            .addComponent(lintFieldsWrapperBuilder.panel)
            .addSeparator(4)
            .addVerticalGap(4)
            .panel

        val centerPanel = SwingHelper.wrapWithHorizontalStretch(panel)
        centerPanel.border = BorderFactory.createEmptyBorder(5, 0, 0, 0)

        return centerPanel
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

    private fun detectPython(): NotNullProducer<List<String>> = NotNullProducer { arrayListOf() }

    private fun detectLinters(): NotNullProducer<List<String>> = NotNullProducer { arrayListOf() }

    private fun disabledBehavior(): Consumer<LinterConfig> = Consumer {
        pythonPathField.isEnabled = LinterConfig.CUSTOM == it
        linterPathField.isEnabled = LinterConfig.CUSTOM == it
        argumentsField.isEnabled = LinterConfig.GLOBAL == it || LinterConfig.CUSTOM == it
    }

    override fun isModified(): Boolean {
        return !Objects.equals(pythonPathField.text, Settings[OPTION_KEY_PYTHON])
                || !Objects.equals(linterPathField.text, Settings[OPTION_KEY_SQLLINT])
                || !Objects.equals(argumentsField.text, Settings[OPTION_KEY_SQLLINT_ARGUMENTS])
                || globalConfigView.isModified()
    }

    override fun reset() {
        pythonPathField.text = Settings[OPTION_KEY_PYTHON]
        linterPathField.text = Settings[OPTION_KEY_SQLLINT]
        argumentsField.text = Settings[OPTION_KEY_SQLLINT_ARGUMENTS]
        globalConfigView.reset()
    }

    override fun apply() {
        Settings[OPTION_KEY_PYTHON] = pythonPathField.text
        Settings[OPTION_KEY_SQLLINT] = linterPathField.text
        Settings[OPTION_KEY_SQLLINT_ARGUMENTS] = argumentsField.text
        globalConfigView.apply()
    }
}