package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile

object CustomLinter: Linter() {
    override fun lint(file: PsiFile, document: Document): List<LinterExternalAnnotator.Error> {
        TODO("Not yet implemented")
    }

    override fun buildCommandLineArgs(python: String, lint: String, lintOptions: String, file: PsiFile): List<String> {
        TODO("Not yet implemented")
    }
}