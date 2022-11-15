package co.anbora.labs.sqlfluff.lint

import com.intellij.codeInspection.InspectionManager
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.psi.PsiFile

class LinterInspection: LocalInspectionTool() {

    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
        if (!file.isSqlFileType()) {
            return arrayOf()
        }

        val document = FileDocumentManager.getInstance().getDocument(file.virtualFile)
            ?: return arrayOf()

        return LintRunner.lint(file, manager, document).toTypedArray()
    }



}