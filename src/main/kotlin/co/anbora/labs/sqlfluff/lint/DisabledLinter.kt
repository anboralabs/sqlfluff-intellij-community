package co.anbora.labs.sqlfluff.lint

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile

object DisabledLinter: Linter() {
    override fun lint(
        file: PsiFile,
        manager: InspectionManager,
        document: Document
    ): List<ProblemDescriptor> = listOf()

    override fun buildCommandLineArgs(
        python: String,
        lint: String,
        lintOptions: String,
        file: PsiFile
    ): List<String> = listOf()
}