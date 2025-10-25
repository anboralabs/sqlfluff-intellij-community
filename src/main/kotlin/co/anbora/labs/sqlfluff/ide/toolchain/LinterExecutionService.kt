package co.anbora.labs.sqlfluff.ide.toolchain

import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute

@State(
    name = "Sqlfluff Toolchain",
    storages = [Storage("NewSqlFluffExecutionsHome.xml")]
)
class LinterExecutionService(
    private val project: Project
): PersistentStateComponent<LinterExecutionService.ToolchainState?> {
    private var state = ToolchainState()

    val linter: LinterConfig
        get() = state.linter
    val configLocation: String
        get() = state.configPath
    val executeWhenSave: Boolean
        get() = state.executeWhenSave

    fun setLinterSettingOption(options: LinterConfigSettings) {
        state.linter = options.linter
        state.configPath = options.configPath
        state.executeWhenSave = options.executeWhenSave
    }

    fun setConfigPath(configPath: String) {
        state.configPath = configPath
    }

    override fun getState() = state

    override fun loadState(state: ToolchainState) {
        XmlSerializerUtil.copyBean(state, this.state)
    }

    class ToolchainState {
        @Attribute("linter")
        var linter: LinterConfig = LinterConfig.GLOBAL
        @Attribute("configPath")
        var configPath: String = ""
        @Attribute("executeWhenSave")
        var executeWhenSave: Boolean = true
    }

    class LinterConfigSettings(
        val linter: LinterConfig,
        val configPath: String,
        val executeWhenSave: Boolean
    )
}
