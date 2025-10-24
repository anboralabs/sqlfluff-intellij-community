package co.anbora.labs.sqlfluff.ide.ui

import co.anbora.labs.sqlfluff.ide.toolchain.LinterExecutionService
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.panel
import java.util.function.Consumer
import javax.swing.ButtonGroup

class ExecuteWhenView(
    val changeListener: Consumer<Boolean>,
    private val settings: LinterExecutionService
) {

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

    fun getComponent(): DialogPanel {
        return panel {
            group("Execute When") {
                buttonsGroup {
                    row { cell(executeWhenTyping) }
                    row { cell(executeWhenSave) }
                }
            }
        }
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
        selectExecution(settings.executeWhenSave)
    }
}
