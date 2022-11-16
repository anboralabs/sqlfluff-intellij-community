package co.anbora.labs.sqlfluff.settings

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
import java.util.function.Supplier
import javax.swing.BorderFactory
import javax.swing.JComponent
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.plaf.basic.BasicComboBoxEditor


class LinterSettings: Configurable {

    val OPTION_KEY_PYTHON = "python"
    val OPTION_KEY_SQLLINT = "sqlFluffLint"
    val OPTION_KEY_SQLLINT_OPTIONS = "sqlLintOptions"

    private var modified = false

    private lateinit var jTextSqlLintOptions: JTextField

    private lateinit var pythonPathField: TextFieldWithHistoryWithBrowseButton
    private lateinit var linterPathField: TextFieldWithHistoryWithBrowseButton

    private lateinit var rdDisableLint: JBRadioButton
    private lateinit var rdUseGlobalLinter: JBRadioButton
    private lateinit var rdUseManualLinter: JBRadioButton

    private val listener = OptionModifiedListener(this)

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

        TextFieldWithHistoryWithBrowseButton()

        jTextSqlLintOptions = JTextField("", 39)


        val lintFieldsWrapperBuilder = FormBuilder.createFormBuilder()
            .setHorizontalGap(UIUtil.DEFAULT_HGAP)
            .setVerticalGap(UIUtil.DEFAULT_VGAP)

        lintFieldsWrapperBuilder.addLabeledComponent("Python path:", pythonPathField)
            .addLabeledComponent("sqlfluff.py path:", linterPathField)


        val builder = FormBuilder.createFormBuilder()
            .setHorizontalGap(UIUtil.DEFAULT_HGAP)
            .setVerticalGap(UIUtil.DEFAULT_VGAP)

        val panel = builder.addComponent(lintFieldsWrapperBuilder.getPanel())
            //.addComponent(myConfigFileView.getComponent())
            .addSeparator(4)
            .addVerticalGap(4)
            .panel

        val centerPanel = SwingHelper.wrapWithHorizontalStretch(panel)

        centerPanel.border = BorderFactory.createEmptyBorder(5, 0, 0, 0)

        return centerPanel


        /*val jPanel = JPanel()

        val verticalLayout = VerticalLayout(1, 2)
        jPanel.layout = verticalLayout

        val jLabelCpplintOptions = JLabel("sqlfluff.py options:")
        jTextSqlLintOptions = JTextField("", 39)

        reset()

        jFilePickerPython.addDocumentListener(listener)
        jFilePickerSqlLint.addDocumentListener(listener)
        jTextSqlLintOptions.document.addDocumentListener(listener)

        jPanel.add(jFilePickerPython)
        jPanel.add(jFilePickerSqlLint)
        jPanel.add(jLabelCpplintOptions)
        jPanel.add(jTextSqlLintOptions)

        return jPanel*/
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

    override fun isModified(): Boolean = modified

    fun setModified(modified: Boolean) {
        this.modified = modified
    }

    override fun reset() {
        super.reset()
    }

    override fun disposeUIResources() {
        super.disposeUIResources()
    }

    override fun apply() {
        TODO("Not yet implemented")
    }

    private class OptionModifiedListener(option: LinterSettings) : DocumentListener {
        private val option: LinterSettings

        init {
            this.option = option
        }

        override fun insertUpdate(documentEvent: DocumentEvent) {
            option.isModified = true
        }

        override fun removeUpdate(documentEvent: DocumentEvent) {
            option.isModified = true
        }

        override fun changedUpdate(documentEvent: DocumentEvent) {
            option.isModified = true
        }
    }
}