package co.anbora.labs.sqlfluff

import com.intellij.lang.Language
import com.intellij.openapi.util.io.StreamUtil

object LinterConfigLanguage: Language("sql_fluff_config") {
    private fun readResolve(): Any = LinterConfigLanguage

    const val LANGUAGE_NAME = "sqlfluff config"

    val LANGUAGE_DEMO_TEXT by lazy {
        val stream = javaClass.classLoader.getResourceAsStream("demo/.sqlfluff")
            ?: error("No such file")
        val text = stream.bufferedReader().use { it.readText() }
        StreamUtil.convertSeparators(text)
    }
}