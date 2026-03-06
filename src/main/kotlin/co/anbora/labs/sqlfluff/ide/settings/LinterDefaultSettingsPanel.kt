package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.ide.toolchain.LinterExecutionService
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService.Companion.toolchainSettings
import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.openapi.components.service
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.ui.DialogPanel
import javax.swing.JComponent

class LinterDefaultSettingsPanel : LinterSettingsPanel {

    private lateinit var mainPanel: DialogPanel
    private lateinit var model: LinterProjectSettingsForm.Model
    private lateinit var settingsForm: LinterProjectSettingsForm

    private lateinit var project: Project

    override fun createPanel(project: Project): JComponent {
        this.project = project

        this.model = LinterProjectSettingsForm.Model(
            homeLocation = "",
            linter = LinterConfig.GLOBAL,
            configPath = "",
            executeWhenSave = true
        )
        this.settingsForm = LinterProjectSettingsForm(project, model)
        this.mainPanel = settingsForm.createComponent()

        return this.mainPanel
    }

    override fun isModified(): Boolean {
        this.mainPanel.apply()

        val settings = project.service<LinterExecutionService>()
        return model.homeLocation != toolchainSettings.toolchainLocation
                || model.executeWhenSave != settings.executeWhenSave
                || model.linter != settings.linter
                || model.configPath != settings.configLocation
    }

    override fun apply() {
        this.mainPanel.apply()

        val issues = this.mainPanel.validateAll()
        if (issues.isNotEmpty()) {
            throw ConfigurationException(issues.first().message)
        }

        val settings = project.service<LinterExecutionService>()
        toolchainSettings.setToolchain(LinterToolchain.fromPath(this.model.homeLocation))
        settings.setLinterSettingOption(
            LinterExecutionService.LinterConfigSettings(
                this.model.linter,
                this.model.configPath,
                this.model.executeWhenSave
            )
        )
    }

    override fun reset() {
        val settings = project.service<LinterExecutionService>()
        with(this.model) {
            homeLocation = toolchainSettings.toolchainLocation
            linter = settings.linter
            configPath = settings.configLocation
            executeWhenSave = settings.executeWhenSave
        }
        settingsForm.reset()
        mainPanel.reset()
    }

    override fun getOrder(): Int = 0
}
