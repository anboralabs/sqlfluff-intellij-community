package co.anbora.labs.sqlfluff.file

import co.anbora.labs.sqlfluff.LinterConfigLanguage
import co.anbora.labs.sqlfluff.LinterConfigLanguage.LANGUAGE_NAME
import co.anbora.labs.sqlfluff.ide.icons.LinterIcons
import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object LinterFileType: LanguageFileType(LinterConfigLanguage) {

    private const val EXTENSION = "sqlfluff"

    override fun getName(): String = LANGUAGE_NAME

    override fun getDescription(): String = "Config file sqlfluff"

    override fun getDefaultExtension(): String = EXTENSION

    override fun getIcon(): Icon = LinterIcons.SQL_FLUFF_16
}