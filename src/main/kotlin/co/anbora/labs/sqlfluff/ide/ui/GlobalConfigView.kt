package co.anbora.labs.sqlfluff.ide.ui

import co.anbora.labs.sqlfluff.ide.settings.Settings
import co.anbora.labs.sqlfluff.ide.settings.Settings.SELECTED_LINTER
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.ui.IdeBorderFactory
import com.intellij.ui.components.JBRadioButton
import com.intellij.util.ui.FormBuilder
import java.util.Objects
import java.util.function.Consumer
import javax.swing.ButtonGroup
import javax.swing.JPanel

class GlobalConfigView(val changeListener: Consumer<LinterConfig>) {

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

        buttonGroup.add(disableLint)
        buttonGroup.add(useGlobalLint)
        buttonGroup.add(useManualLint)

        disableLint.addActionListener {
            changeListener.accept(LinterConfig.DISABLED)
        }

        useGlobalLint.addActionListener {
            changeListener.accept(LinterConfig.GLOBAL)
        }

        useManualLint.addActionListener {
            changeListener.accept(LinterConfig.CUSTOM)
        }
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

    fun selectedLinterConfig(): LinterConfig {
        return when {
            useGlobalLint.isSelected -> LinterConfig.GLOBAL
            useManualLint.isSelected -> LinterConfig.CUSTOM
            else -> LinterConfig.DISABLED
        }
    }

    fun selectLinter(linter: LinterConfig) {
        useGlobalLint.isSelected = LinterConfig.GLOBAL == linter
        useManualLint.isSelected = LinterConfig.CUSTOM == linter
        disableLint.isSelected = LinterConfig.DISABLED == linter
        changeListener.accept(linter)
    }

    fun isModified(): Boolean {
        return !Objects.equals(selectedLinterConfig().name, Settings[SELECTED_LINTER])
    }

    fun reset() {
        selectLinter(LinterConfig.getOrDefault(Settings[SELECTED_LINTER]))
    }

    fun apply() {
        Settings[SELECTED_LINTER] = selectedLinterConfig().name
    }

}
