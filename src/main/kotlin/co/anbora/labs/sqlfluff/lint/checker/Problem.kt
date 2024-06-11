package co.anbora.labs.sqlfluff.lint.checker

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.annotation.AnnotationBuilder
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

sealed class Problem(private val message: String) {
    fun getMessage(): String = message

    abstract fun createAnnotation(holder: AnnotationHolder)
}

class PsiProblem(
    private val message: String,
    val psiElement: PsiElement,
    val severity: HighlightSeverity,
    val fix: IntentionAction?
): Problem(message) {
    override fun createAnnotation(holder: AnnotationHolder) {
        var annotation: AnnotationBuilder = holder
            .newAnnotation(severity, message)
            .range(psiElement)
        if (fix != null) {
            annotation = annotation.newFix(fix).registerFix()
        }
        annotation.create()
    }
}

class TextRangeProblem(
    private val message: String,
    val range: TextRange,
    val severity: HighlightSeverity,
    val afterEndOfLine: Boolean
): Problem(message) {
    override fun createAnnotation(holder: AnnotationHolder) {
        var annotation: AnnotationBuilder = holder
            .newAnnotation(severity, message)
            .range(range)
        if (afterEndOfLine) {
            annotation = annotation.afterEndOfLine()
        }
        annotation.create()
    }
}
