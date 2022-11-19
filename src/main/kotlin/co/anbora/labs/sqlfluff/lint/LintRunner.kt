package co.anbora.labs.sqlfluff.lint

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile

object LintRunner {

    fun lint(
        file: PsiFile,
        manager: InspectionManager,
        document: Document
    ): List<ProblemDescriptor> {
        return listOf()
    }

}