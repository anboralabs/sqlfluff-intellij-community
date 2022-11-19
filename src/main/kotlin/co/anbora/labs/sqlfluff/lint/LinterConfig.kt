package co.anbora.labs.sqlfluff.lint

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile

enum class LinterConfig(protected val linter: Linter) {

    DISABLED(DisabledLinter) {
        override fun lint(
            file: PsiFile,
            manager: InspectionManager,
            document: Document
        ): List<ProblemDescriptor> = linter.lint(file, manager, document)
    },
    GLOBAL(GlobalLinter) {
        override fun lint(
            file: PsiFile,
            manager: InspectionManager,
            document: Document
        ): List<ProblemDescriptor> = linter.lint(file, manager, document)
    },
    CUSTOM(CustomLinter) {
        override fun lint(
            file: PsiFile,
            manager: InspectionManager,
            document: Document
        ): List<ProblemDescriptor> = linter.lint(file, manager, document)
    },;

    abstract fun lint(
        file: PsiFile,
        manager: InspectionManager,
        document: Document
    ): List<ProblemDescriptor>

}