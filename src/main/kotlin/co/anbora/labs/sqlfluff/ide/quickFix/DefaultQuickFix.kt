package co.anbora.labs.sqlfluff.ide.quickFix

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.psi.PsiElement

object DefaultQuickFix: QuickFixFlavor() {
    override fun suggestFix(errorType: String, psiElement: PsiElement): BaseIntentionAction? = null
}