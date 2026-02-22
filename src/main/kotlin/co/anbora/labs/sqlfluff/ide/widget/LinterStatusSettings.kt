package co.anbora.labs.sqlfluff.ide.widget

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

@State(name = "SqlfluffLinterStatusSettings", storages = [Storage("sqlfluff_linter_status.xml")])
@Service(Service.Level.PROJECT)
class LinterStatusSettings(private val project: Project) : PersistentStateComponent<LinterStatusSettings.State> {

    data class State(
        var showWidget: Boolean = true
    )

    private var myState: State = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    var showWidget: Boolean
        get() = myState.showWidget
        set(value) {
            myState.showWidget = value
        }
}
