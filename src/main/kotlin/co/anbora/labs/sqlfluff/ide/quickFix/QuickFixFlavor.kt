package co.anbora.labs.sqlfluff.ide.quickFix

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.psi.PsiElement


abstract class QuickFixFlavor {
    abstract fun suggestFix(errorType: String, psiElement: PsiElement): BaseIntentionAction?

    companion object {
        private val EP_NAME: ExtensionPointName<QuickFixFlavor> =
            ExtensionPointName.create("co.anbora.labs.sqlfluff.quickFixer")

        fun getApplicableFlavor(): QuickFixFlavor =
            EP_NAME.extensionList.firstOrNull() ?: DefaultQuickFix
    }
}
