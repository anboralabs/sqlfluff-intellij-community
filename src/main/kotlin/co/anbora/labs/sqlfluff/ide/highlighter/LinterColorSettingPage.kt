package co.anbora.labs.sqlfluff.ide.highlighter

import co.anbora.labs.sqlfluff.LinterConfigLanguage.LANGUAGE_DEMO_TEXT
import co.anbora.labs.sqlfluff.ide.icons.LinterIcons
import ini4idea.highlighter.IniColorSettingsPage
import javax.swing.Icon

class LinterColorSettingPage: IniColorSettingsPage() {

    override fun getDisplayName(): String = "Sqlfluff Linter"

    override fun getIcon(): Icon = LinterIcons.SQL_FLUFF_16

    override fun getDemoText(): String = LANGUAGE_DEMO_TEXT
}
