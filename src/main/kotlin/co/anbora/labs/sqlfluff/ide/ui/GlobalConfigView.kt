package co.anbora.labs.sqlfluff.ide.ui

import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBRadioButton
import com.intellij.util.ui.FormBuilder
import javax.swing.ButtonGroup
import javax.swing.JPanel

class GlobalConfigView {

    private lateinit var disableLint: JBRadioButton
    private lateinit var useGlobalLint: JBRadioButton
    private lateinit var useManualLint: JBRadioButton

    init {
        createUI()
    }

    private fun createUI() {
        val buttonGroup = ButtonGroup()
        disableLint = JBRadioButton("Disable linter")
        useGlobalLint = JBRadioButton("Use sqlfluff global (Recommended)")
        useManualLint = JBRadioButton("Use sqlfluff manual")

        useGlobalLint.isSelected = true

        buttonGroup.add(disableLint)
        buttonGroup.add(useGlobalLint)
        buttonGroup.add(useManualLint)
    }

    fun getComponent(): JPanel {
        val formBuilder = FormBuilder.createFormBuilder()

        formBuilder.addComponent(disableLint)
            .addComponent(useGlobalLint)
            .addComponent(useManualLint)

        val panel = formBuilder.panel

        panel.border = IdeBorderFactory.createTitledBorder("Global Config")

        return panel
    }

}