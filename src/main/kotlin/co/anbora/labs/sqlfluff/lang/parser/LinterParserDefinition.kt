package co.anbora.labs.sqlfluff.lang.parser

import co.anbora.labs.sqlfluff.LinterConfigLanguage
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.lexer.Lexer
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet
import ini4idea.lang.IniElementTypes
import ini4idea.lang.IniTokenTypes
import ini4idea.lang.lexer.IniLexer
import ini4idea.lang.parser.IniGeneratedParser

class LinterParserDefinition: ParserDefinition {

    private val type = IFileElementType(LinterConfigLanguage)

    override fun createLexer(project: Project?): Lexer = IniLexer()

    override fun createParser(project: Project?): PsiParser = IniGeneratedParser()

    override fun getFileNodeType(): IFileElementType = type

    override fun getCommentTokens(): TokenSet = IniTokenTypes.COMMENTS

    override fun getWhitespaceTokens(): TokenSet = IniTokenTypes.WHITESPACES

    override fun getStringLiteralElements(): TokenSet = IniTokenTypes.STRING_LITERALS

    override fun createElement(node: ASTNode?): PsiElement = IniElementTypes.Factory.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = LinterConfigFile(viewProvider)
}
