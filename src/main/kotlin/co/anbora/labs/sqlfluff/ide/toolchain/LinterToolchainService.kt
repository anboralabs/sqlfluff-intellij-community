package co.anbora.labs.sqlfluff.ide.toolchain

import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.openapi.components.*
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Attribute

@State(
    name = "Sqlfluff Toolchain",
    storages = [Storage("NewSqlFluffHome.xml")]
)
class LinterToolchainService: PersistentStateComponent<LinterToolchainService.ToolchainState?> {
    private var state = ToolchainState()
    val toolchainLocation: String
        get() = state.toolchainLocation

    val linter: LinterConfig
        get() = state.linter
    val configLocation: String
        get() = state.configPath
    val executeWhenSave: Boolean
        get() = state.executeWhenSave

    @Volatile
    private var toolchain: LinterToolchain = LinterToolchain.NULL

    fun setToolchain(newToolchain: LinterToolchain) {
        toolchain = newToolchain
        state.toolchainLocation = newToolchain.homePath()
        state.toolchainVersion = newToolchain.version()
    }

    fun setLinterSettingOption(options: LinterConfigSettings) {
        state.linter = options.linter
        state.configPath = options.configPath
        state.executeWhenSave = options.executeWhenSave
    }

    fun toolchain(): LinterToolchain {
        if (toolchain == LinterToolchain.NULL && state.isValid()) {
            setToolchain(LinterToolchain.fromPath(state.toolchainLocation))
        }
        return toolchain
    }

    fun cachedToolchain(): LinterToolchain {
        if (toolchain == LinterToolchain.NULL && state.isValid()) {
            setToolchain(LinterToolchain.fromState(state))
        }
        return toolchain
    }

    fun isNotSet(): Boolean = toolchain == LinterToolchain.NULL

    override fun getState() = state

    override fun loadState(state: ToolchainState) {
        XmlSerializerUtil.copyBean(state, this.state)
    }

    companion object {
        val toolchainSettings
            get() = service<LinterToolchainService>()
    }

    class ToolchainState {
        @Attribute("url")
        var toolchainLocation: String = ""
        @Attribute("version")
        var toolchainVersion: String = ""

        @Attribute("linter")
        var linter: LinterConfig = LinterConfig.GLOBAL
        @Attribute("configPath")
        var configPath: String = ""
        @Attribute("executeWhenSave")
        var executeWhenSave: Boolean = true

        fun isValid(): Boolean {
            return toolchainVersion.isNotEmpty() && toolchainLocation.isNotEmpty()
        }
    }

    class LinterConfigSettings(
        val linter: LinterConfig,
        val configPath: String,
        val executeWhenSave: Boolean
    )
}
