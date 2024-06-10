package co.anbora.labs.sqlfluff.lint.checker

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement

data class Problem(
    val startLine: Int,
    val endLine: Int?,
    val message: String,
    val psiElement: PsiElement,
    val severity: HighlightSeverity,
    val fix: IntentionAction?
)