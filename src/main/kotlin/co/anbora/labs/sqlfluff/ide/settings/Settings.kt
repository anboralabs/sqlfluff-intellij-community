package co.anbora.labs.sqlfluff.ide.settings

import co.anbora.labs.sqlfluff.lint.LinterConfig
import com.intellij.ide.util.PropertiesComponent

object Settings {

    val OPTION_KEY_PYTHON = "python"
    val OPTION_KEY_SQLLINT = "sqlFluffLint"
    val OPTION_KEY_SQLLINT_ARGUMENTS = "sqlLintOptions"
    val DEFAULT_ARGUMENTS = "--dialect ansi"

    val SELECTED_LINTER = "selectedLinter"

    private val INSTANCE = PropertiesComponent.getInstance()

    init {
        if (INSTANCE.getValue(OPTION_KEY_SQLLINT_ARGUMENTS).isNullOrBlank()) {
            INSTANCE.setValue(OPTION_KEY_SQLLINT_ARGUMENTS, DEFAULT_ARGUMENTS)
        }
        if (INSTANCE.getValue(SELECTED_LINTER).isNullOrBlank()) {
            INSTANCE.setValue(SELECTED_LINTER, LinterConfig.GLOBAL.name)
        }
    }
    operator fun set(key: String, value: String?) {
        INSTANCE.setValue(key, value)
    }

    operator fun get(key: String): String {
        return INSTANCE.getValue(key).orEmpty()
    }
}
