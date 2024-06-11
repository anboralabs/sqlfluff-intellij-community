package co.anbora.labs.sqlfluff.ide.ui

import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBRadioButton
import com.intellij.util.ui.FormBuilder
import java.util.function.Consumer
import javax.swing.ButtonGroup
import javax.swing.JPanel

class ExecuteWhenView(val changeListener: Consumer<Boolean>) {

    private lateinit var executeWhenTyping: JBRadioButton
    private lateinit var executeWhenSave: JBRadioButton

    init {
        createUI()
    }

    private fun createUI() {
        val buttonGroup = ButtonGroup()
        executeWhenTyping = JBRadioButton("Execute when typing")
        executeWhenSave = JBRadioButton("Execute when save (Recommended)")

        buttonGroup.add(executeWhenTyping)
        buttonGroup.add(executeWhenSave)

        executeWhenTyping.addActionListener {
            changeListener.accept(false)
        }

        executeWhenSave.addActionListener {
            changeListener.accept(true)
        }
    }

    fun getComponent(): JPanel {
        val formBuilder = FormBuilder.createFormBuilder()

        formBuilder.addComponent(executeWhenTyping)
            .addComponent(executeWhenSave)

        val panel = formBuilder.panel

        panel.border = IdeBorderFactory.createTitledBorder("Execute When")

        return panel
    }

    fun selectExecution(onSave: Boolean) {
        executeWhenSave.isSelected = onSave
        executeWhenTyping.isSelected = !onSave
        changeListener.accept(onSave)
    }

    fun setEnable(enabled: Boolean) {
        executeWhenSave.isEnabled = enabled
        executeWhenTyping.isEnabled = enabled
    }

    fun reset() {
        val settings = toolchainSettings
        selectExecution(settings.executeWhenSave)
    }
}
