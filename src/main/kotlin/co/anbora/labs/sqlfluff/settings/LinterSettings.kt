package co.anbora.labs.sqlfluff.settings

import co.anbora.labs.sqlfluff.ui.JFilePicker
import com.intellij.openapi.options.Configurable
import com.intellij.ui.components.panels.VerticalLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class LinterSettings: Configurable {

    val OPTION_KEY_PYTHON = "python"
    val OPTION_KEY_SQLLINT = "sqlFluffLint"
    val OPTION_KEY_SQLLINT_OPTIONS = "sqlLintOptions"

    private var modified = false

    private lateinit var jFilePickerPython: JFilePicker
    private lateinit var jFilePickerSqlLint: JFilePicker
    private lateinit var jTextSqlLintOptions: JTextField

    private val listener = OptionModifiedListener(this)


    override fun getDisplayName(): String = "Sqlfluff"

    override fun getHelpTopic(): String? = null

    override fun createComponent(): JComponent {
        val jPanel = JPanel()

        val verticalLayout = VerticalLayout(1, 2)
        jPanel.layout = verticalLayout

        jFilePickerPython = JFilePicker("Python path:", "...")
        jFilePickerSqlLint = JFilePicker("sqlfluff.py path:", "...")
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

        return jPanel
    }

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