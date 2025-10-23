package co.anbora.labs.sqlfluff.ide.listener

import com.intellij.openapi.Disposable
import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class LinterListenerService : Disposable {
    override fun dispose() = Unit
}
