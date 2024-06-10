package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile

fun isSqlFileType(config: LinterConfigFile?, extension: String): Boolean {
    return config != null && extension in config.extensions()
}