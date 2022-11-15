package co.anbora.labs.sqlfluff.ui

import java.awt.FlowLayout
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.event.DocumentListener

class JFilePicker(private val textFieldLabel: String, private val buttonLabel: String) : JPanel() {
    private val label: JLabel
    private val textField: JTextField
    private val button: JButton
    val fileChooser: JFileChooser
    private var mode: Int

    init {
        fileChooser = JFileChooser()
        layout = FlowLayout(FlowLayout.CENTER, 5, 5)

        // creates the GUI
        label = JLabel(textFieldLabel)
        textField = JTextField(30)
        button = JButton(buttonLabel)
        button.addActionListener { evt -> buttonActionPerformed(evt) }
        add(label)
        add(textField)
        add(button)
        mode = MODE_OPEN
    }

    private fun buttonActionPerformed(evt: ActionEvent) {
        if (mode == MODE_OPEN) {
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                textField.text = fileChooser.selectedFile.absolutePath
            }
        } else if (mode == MODE_SAVE) {
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                textField.text = fileChooser.selectedFile.absolutePath
            }
        }
    }

    fun addFileTypeFilter(extension: String?, description: String?) {
//        FileTypeFilter filter = new FileTypeFilter(OCFileType.INSTANCE);
//        fileChooser.addChoosableFileFilter(filter);
    }

    fun setMode(mode: Int) {
        this.mode = mode
    }

    val selectedFilePath: String
        get() = textField.text

    fun addDocumentListener(listener: DocumentListener) {
        textField.document.addDocumentListener(listener)
    }

    companion object {
        const val MODE_OPEN = 1
        const val MODE_SAVE = 2
    }
}