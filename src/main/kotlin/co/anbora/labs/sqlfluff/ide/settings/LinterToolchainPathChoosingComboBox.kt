package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.ide.settings.ui.LinterRenderer
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.ComponentWithBrowseButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.awt.event.ActionListener
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile

class LinterToolchainPathChoosingComboBox(
    browseActionListener: ActionListener,
    onItemSelected: (path: Path) -> Unit = {}
): ComponentWithBrowseButton<ComboBox<Path>>(ComboBox(), browseActionListener) {

    private val comboBox = childComponent

    init {
        comboBox.isEditable = true
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
            select(toolchains.firstOrNull())
        }
    }
}
