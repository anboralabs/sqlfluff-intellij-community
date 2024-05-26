package co.anbora.labs.sqlfluff.ide.toolchain

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

    @Volatile
    private var toolchain: LinterToolchain = LinterToolchain.NULL

    fun setToolchain(newToolchain: LinterToolchain) {
        toolchain = newToolchain
        state.toolchainLocation = newToolchain.homePath()
    }

    fun toolchain(): LinterToolchain {
        val currentLocation = state.toolchainLocation
        if (toolchain == LinterToolchain.NULL && currentLocation.isNotEmpty()) {
            setToolchain(LinterToolchain.fromPath(currentLocation))
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
    }
}
