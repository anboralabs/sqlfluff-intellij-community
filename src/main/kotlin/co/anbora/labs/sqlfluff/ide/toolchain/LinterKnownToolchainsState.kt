package co.anbora.labs.sqlfluff.ide.toolchain

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "SqlFluff Home",
    storages = [Storage("NewSqlFluffToolchains.xml")]
)
class LinterKnownToolchainsState : PersistentStateComponent<LinterKnownToolchainsState?> {
    companion object {
        fun getInstance() = service<LinterKnownToolchainsState>()
    }

    var knownToolchains: Set<String> = emptySet()

    fun isKnown(homePath: String): Boolean {
        return knownToolchains.contains(homePath)
    }

    fun add(toolchain: LinterToolchain) {
        knownToolchains = knownToolchains + toolchain.homePath()
    }

    override fun getState() = this

    override fun loadState(state: LinterKnownToolchainsState) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
