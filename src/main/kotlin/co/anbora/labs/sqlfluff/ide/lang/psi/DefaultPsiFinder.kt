package co.anbora.labs.sqlfluff.ide.lang.psi

import co.anbora.labs.sqlfluff.lint.issue.Issue
import com.intellij.openapi.editor.Document
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

object DefaultPsiFinder: PsiFinderFlavor() {
    override fun findElement(psiFile: PsiFile, document: Document, issue: Issue): Pair<TextRange?, PsiElement?> {
        val lineCount = document.lineCount
        if (0 == lineCount) {
            return Pair(null, psiFile)
        }

        val start  = getStart(document, issue)
        val end = getEnd(start, issue)

        return Pair(TextRange.create(start, end), null)
    }

    private fun getStart(document: Document, issue: Issue): Int {
        var lineNumber = issue.lineNo ?: 0
        lineNumber = if (lineNumber > 0) lineNumber - 1 else lineNumber

        val position = issue.linePos ?: 0
        val lineStartOffset = document.getLineStartOffset(lineNumber)
        val initialPosition = if (position > 0) position - 1 else 0

        return lineStartOffset + initialPosition
    }

    private fun getEnd(startPosition: Int, issue: Issue): Int {

        return issue.endFilePos ?: startPosition
    }
}
