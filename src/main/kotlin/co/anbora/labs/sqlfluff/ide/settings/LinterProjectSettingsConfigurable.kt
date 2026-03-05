package co.anbora.labs.sqlfluff.ide.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import javax.swing.BorderFactory
import javax.swing.BoxLayout
import javax.swing.JComponent
import javax.swing.JPanel

class LinterProjectSettingsConfigurable(private val project: Project) : Configurable {

    private val providers: List<LinterSettingsPanel> by lazy {
        LinterSettingsPanel.EP_NAME.extensionList.sortedBy { it.getOrder() }
    }

    private lateinit var rootPanel: JPanel

    override fun createComponent(): JComponent {
        rootPanel = panel {
            for (provider in providers) {
                row {
                    cell(provider.createPanel(project))
                        .align(AlignX.FILL)
                }
            }
        }.withBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0))

        return rootPanel
    }

    override fun getPreferredFocusedComponent(): JComponent = rootPanel

    override fun isModified(): Boolean {
        return providers.any { it.isModified() }
    }

    override fun apply() {
        for (panel in providers) {
            panel.apply()
        }
    }

    override fun reset() {
        for (panel in providers) {
            panel.reset()
        }
    }

    override fun getDisplayName(): String = "SQLFluff Linter"

    companion object {
        @JvmStatic
        fun show(project: Project) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, LinterProjectSettingsConfigurable::class.java)
        }
    }
}
