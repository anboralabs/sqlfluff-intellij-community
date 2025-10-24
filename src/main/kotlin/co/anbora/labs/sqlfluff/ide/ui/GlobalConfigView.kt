package co.anbora.labs.sqlfluff.ide.ui

import co.anbora.labs.sqlfluff.ide.toolchain.LinterExecutionService
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.components.JBRadioButton
import com.intellij.ui.dsl.builder.panel
import java.util.function.Consumer
import javax.swing.ButtonGroup

class GlobalConfigView(
    val changeListener: Consumer<LinterConfig>,
    private val settings: LinterExecutionService
) {

    private lateinit var disableLint: JBRadioButton
    private lateinit var useGlobalLint: JBRadioButton
    private lateinit var useManualLint: JBRadioButton

    init {
        createUI()
    }

    private fun createUI() {
        val buttonGroup = ButtonGroup()
        disableLint = JBRadioButton("Disable linter")
        useGlobalLint = JBRadioButton("Use default .sqlfluff")
        useManualLint = JBRadioButton("Use project .sqlfluff (Recommended)")

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

    fun getComponent(): DialogPanel {
        return panel {
            group("Global Config") {
                buttonsGroup {
                    row { cell(disableLint) }
                    row { cell(useGlobalLint) }
                    row { cell(useManualLint) }
                }
            }
        }
    }

    private fun selectLinter(linter: LinterConfig) {
        useGlobalLint.isSelected = LinterConfig.GLOBAL == linter
        useManualLint.isSelected = LinterConfig.CUSTOM == linter
        disableLint.isSelected = LinterConfig.DISABLED == linter
        changeListener.accept(linter)
    }

    fun reset() {
        selectLinter(settings.linter)
    }
}
