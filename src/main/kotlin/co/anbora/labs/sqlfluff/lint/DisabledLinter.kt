package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile

object DisabledLinter: Linter() {
    override fun lint(
        file: PsiFile,
        document: Document
    ): List<LinterExternalAnnotator.Error> = listOf()

    override fun buildCommandLineArgs(
        python: String,
        lint: String,
        lintOptions: String,
        file: PsiFile
    ): SqlFluffLintRunner.Param = SqlFluffLintRunner.Param()
}