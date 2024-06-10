package co.anbora.labs.sqlfluff.ide.lang.psi

import co.anbora.labs.sqlfluff.lint.issue.Issue
import com.intellij.openapi.editor.Document
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

abstract class PsiFinderFlavor {

    abstract fun findElement(psiFile: PsiFile, document: Document, issue: Issue): Pair<TextRange?, PsiElement?>

    companion object {
        private val EP_NAME: ExtensionPointName<PsiFinderFlavor> =
            ExtensionPointName.create("co.anbora.labs.sqlfluff.elementFinder")

        fun getApplicableFlavor(): PsiFinderFlavor =
            EP_NAME.extensionList.firstOrNull() ?: DefaultPsiFinder
    }

}