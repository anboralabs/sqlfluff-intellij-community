package co.anbora.labs.sqlfluff.settings

import com.intellij.ide.util.PropertiesComponent

object Settings {
    private val INSTANCE = PropertiesComponent.getInstance()
    operator fun set(key: String, value: String?) {
        INSTANCE.setValue(key, value)
    }

    operator fun get(key: String): String? {
        return INSTANCE.getValue(key)
    }
}