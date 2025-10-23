package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.ide.settings.ui.LinterRenderer
import co.anbora.labs.sqlfluff.ide.utils.addTextChangeListener
import co.anbora.labs.sqlfluff.ide.utils.pathAsPath
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.ComboBoxWithWidePopup
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.ComboboxSpeedSearch
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.awt.Component
import java.nio.file.Path
import javax.swing.JList
import javax.swing.ListCellRenderer
import javax.swing.plaf.basic.BasicComboBoxEditor
import kotlin.io.path.pathString

class LinterToolchainPathChoosingComboBox(
    descriptor: FileChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor(),
    onTextChanged: () -> Unit = {}
): ComponentWithBrowseButton<ComboBox<Path>>(ComboBox(), null) {

    /* private val editor: BasicComboBoxEditor = object : BasicComboBoxEditor() {
        override fun createEditorComponent(): ExtendableTextField = ExtendableTextField()
    } */

    private val comboBox = childComponent

    /*private val pathTextField: ExtendableTextField
        get() = childComponent.editor.editorComponent as ExtendableTextField*/

    private val busyIconExtension: ExtendableTextComponent.Extension =
        ExtendableTextComponent.Extension { AnimatedIcon.Default.INSTANCE }

    var selectedPath: String?
        get() = comboBox.item.toString()
        set(value) {
            comboBox.item = Path.of(value ?: "")
        }

    init {
        // ComboboxSpeedSearch.installOn(comboBox)
        // comboBox.editor = editor
        comboBox.isEditable = true
        comboBox.renderer = LinterRenderer<Path>()

        addActionListener {
            FileChooser.chooseFile(descriptor, null, null) { file ->
                comboBox.selectedItem = file.pathAsPath
            }
        }

        comboBox.addItemListener { onTextChanged() }
    }

    /**
     * Obtains a list of toolchains on a pool using [toolchainObtainer], then fills the combobox and calls [callback] on the EDT.
     */
    @Suppress("UnstableApiUsage")
    fun addToolchainsAsync(toolchainObtainer: () -> List<Path>) {
        runBlocking {
            val toolchains = withContext(Dispatchers.Default) {
                try {
                    toolchainObtainer()
                } catch (e: Exception) {
                    emptyList()
                }
            }
            childComponent.removeAllItems()
            toolchains.forEach(childComponent::addItem)
            //selectedPath = selectedPath?.ifEmpty { null } ?: (toolchains.firstOrNull()?.pathString ?: "")
        }
    }
}
