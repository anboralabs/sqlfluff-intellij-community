package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent

class LinterProjectSettingsConfigurable(private val project: Project) : Configurable {

    private val mainPanel: DialogPanel
    private val model = LinterProjectSettingsForm.Model(
        homeLocation = "",
    )
    private val settingsForm = LinterProjectSettingsForm(project, model)

    init {
        mainPanel = settingsForm.createComponent()
    }

    override fun createComponent(): JComponent = mainPanel

    override fun getPreferredFocusedComponent(): JComponent = mainPanel

    override fun isModified(): Boolean {
        mainPanel.apply()

        val settings = toolchainSettings
        return model.homeLocation != settings.toolchainLocation
    }

    override fun apply() {
        mainPanel.apply()

        validateSettings()

        val settings = toolchainSettings
        settings.setToolchain(LinterToolchain.fromPath(model.homeLocation))
    }

    private fun validateSettings() {
        val issues = mainPanel.validateAll()
        if (issues.isNotEmpty()) {
            throw ConfigurationException(issues.first().message)
        }
    }

    override fun reset() {
        val settings = toolchainSettings

        with(model) {
            homeLocation = settings.toolchainLocation
        }

        settingsForm.reset()
        mainPanel.reset()
    }

    override fun getDisplayName(): String = "Sqlfluff Linter"

    companion object {
        @JvmStatic
        fun show(project: Project) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, LinterProjectSettingsConfigurable::class.java)
        }
    }
}