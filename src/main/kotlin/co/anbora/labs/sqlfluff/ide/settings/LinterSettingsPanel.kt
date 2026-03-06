package co.anbora.labs.sqlfluff.ide.settings

import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import javax.swing.JComponent

interface LinterSettingsPanel {

    companion object {
        val EP_NAME: ExtensionPointName<LinterSettingsPanel> =
            ExtensionPointName.create("co.anbora.labs.sqlfluff.settingsPanel")
    }

    fun createPanel(project: Project): JComponent

    fun isModified(): Boolean

    fun apply()

    fun reset()

    fun getOrder(): Int
}
