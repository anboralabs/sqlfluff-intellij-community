package co.anbora.labs.sqlfluff.ide.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurableProvider
import com.intellij.openapi.project.Project

class LinterSettingsConfigurableProvider(private val project: Project) : ConfigurableProvider() {

    override fun canCreateConfigurable(): Boolean {
        return LinterSettingsPanel.EP_NAME.extensionList.isNotEmpty()
    }

    override fun createConfigurable(): Configurable {
        return LinterProjectSettingsConfigurable(project)
    }
}
