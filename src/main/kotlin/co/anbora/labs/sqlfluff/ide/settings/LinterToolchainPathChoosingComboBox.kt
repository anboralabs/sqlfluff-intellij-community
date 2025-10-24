package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.ide.settings.ui.LinterRenderer
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.ComponentWithBrowseButton
import com.intellij.openapi.ui.getPresentablePath
import com.intellij.ui.AnimatedIcon
import com.intellij.ui.components.fields.ExtendableTextComponent
import com.intellij.ui.components.fields.ExtendableTextField
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.awt.event.ActionListener
import java.nio.file.Path
import javax.swing.plaf.basic.BasicComboBoxEditor
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class LinterToolchainPathChoosingComboBox(
    browseActionListener: ActionListener,
    onItemSelected: (path: Path) -> Unit = {}
): ComponentWithBrowseButton<ComboBox<Path>>(ComboBox(), browseActionListener) {

    private val pathTextField: ExtendableTextField
        get() = childComponent.editor.editorComponent as ExtendableTextField

    private val busyIconExtension: ExtendableTextComponent.Extension =
        ExtendableTextComponent.Extension { AnimatedIcon.Default.INSTANCE }

    private val comboBox = childComponent

    init {
        comboBox.isEditable = true

        val editor: BasicComboBoxEditor = object : BasicComboBoxEditor() {
            override fun createEditorComponent(): ExtendableTextField = ExtendableTextField()

            override fun setItem(item: Any?) {
                val text = when (item) {
                    is Path -> getPresentablePath(item.toString())
                    null -> ""
                    else -> item.toString()
                }
                editor?.text = text
            }

            override fun getItem(): Any? {
                return comboBox.selectedItem
            }
        }

        comboBox.editor = editor
        comboBox.renderer = LinterRenderer<Path>()

        comboBox.addItemListener {
            val item = comboBox.selectedItem as? Path ?: return@addItemListener
            onItemSelected(item)
        }
    }

    fun select(location: Path?) {
        val isValid = location != null && (location.isRegularFile() || location.isDirectory())
        if (isValid) {
            comboBox.selectedItem = location
            return
        }

        comboBox.selectedItem = null
    }

    fun selected(): Path? = comboBox.selectedItem as? Path

    private fun setBusy(busy: Boolean) {
        if (busy) {
            pathTextField.addExtension(busyIconExtension)
        } else {
            pathTextField.removeExtension(busyIconExtension)
        }
        repaint()
    }

    /**
     * Obtains a list of toolchains on a pool using [toolchainObtainer], then fills the combobox and calls [callback] on the EDT.
     */
    @Suppress("UnstableApiUsage")
    fun addToolchainsAsync(toolchainObtainer: () -> List<Path>) {
        setBusy(true)
        runBlocking {
            val toolchains = withContext(Dispatchers.Default) {
                try {
                    toolchainObtainer()
                } catch (e: Exception) {
                    emptyList()
                }
            }
            setBusy(false)
            childComponent.removeAllItems()
            toolchains.forEach(childComponent::addItem)
            select(toolchains.firstOrNull())
        }
    }
}
