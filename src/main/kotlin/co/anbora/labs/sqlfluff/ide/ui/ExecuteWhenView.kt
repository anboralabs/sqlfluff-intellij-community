package co.anbora.labs.sqlfluff.ide.ui

import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBRadioButton
import com.intellij.util.ui.FormBuilder
import javax.swing.ButtonGroup
import javax.swing.JPanel

class ExecuteWhenView {

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
    }

    fun getComponent(): JPanel {
        val formBuilder = FormBuilder.createFormBuilder()

        formBuilder.addComponent(executeWhenTyping)
            .addComponent(executeWhenSave)

        val panel = formBuilder.panel

        panel.border = IdeBorderFactory.createTitledBorder("Execute When")

        return panel
    }
}