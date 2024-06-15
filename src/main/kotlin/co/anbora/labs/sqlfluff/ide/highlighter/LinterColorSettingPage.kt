package co.anbora.labs.sqlfluff.ide.highlighter

import co.anbora.labs.sqlfluff.LinterConfigLanguage.LANGUAGE_DEMO_TEXT
import co.anbora.labs.sqlfluff.ide.icons.LinterIcons
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import com.intellij.psi.tree.IElementType
import ini4idea.IniBundle
import ini4idea.highlighter.IniSyntaxHighlighter
import ini4idea.lang.IniTokenTypes
import javax.swing.Icon

class LinterColorSettingPage: ColorSettingsPage {

    private val m: Map<IElementType, TextAttributesKey> = IniSyntaxHighlighter.getTextAttributesMap()

    private val ATTR = arrayOf(
        AttributesDescriptor(
            IniBundle.message("ini.colors.text.key", *arrayOfNulls<Any>(0)),
            (m[IniTokenTypes.KEY_NAME] as TextAttributesKey)
        ), AttributesDescriptor(
            IniBundle.message("ini.colors.text.value", *arrayOfNulls<Any>(0)),
            (m[IniTokenTypes.MULTILINE_VALUE_PART] as TextAttributesKey)
        ), AttributesDescriptor(
            IniBundle.message("ini.colors.text.section", *arrayOfNulls<Any>(0)),
            (m[IniTokenTypes.SECTION_NAME] as TextAttributesKey)
        ), AttributesDescriptor(
            IniBundle.message("ini.colors.text.comment", *arrayOfNulls<Any>(0)),
            (m[IniTokenTypes.ONE_LINE_COMMENT] as TextAttributesKey)
        ), AttributesDescriptor(
            IniBundle.message("ini.colors.text.equal", *arrayOfNulls<Any>(0)),
            (m[IniTokenTypes.ASSIGNMENT_OPERATOR] as TextAttributesKey)
        )
    )


    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = ATTR

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDisplayName(): String = "Sqlfluff Linter"

    override fun getIcon(): Icon = LinterIcons.SQL_FLUFF_16

    override fun getHighlighter(): SyntaxHighlighter = IniSyntaxHighlighter()

    override fun getDemoText(): String = LANGUAGE_DEMO_TEXT

    override fun getAdditionalHighlightingTagToDescriptorMap(): MutableMap<String, TextAttributesKey>? = null
}
