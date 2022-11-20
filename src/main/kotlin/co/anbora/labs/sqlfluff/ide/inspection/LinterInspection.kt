package co.anbora.labs.sqlfluff.ide.inspection

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import com.intellij.codeInspection.*
import com.intellij.codeInspection.ex.UnfairLocalInspectionTool
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile

class LinterInspection: LocalInspectionTool(), BatchSuppressableTool, UnfairLocalInspectionTool {

    override fun checkFile(
        file: PsiFile,
        manager: InspectionManager,
        isOnTheFly: Boolean
    ): Array<ProblemDescriptor> = ExternalAnnotatorInspectionVisitor.checkFileWithExternalAnnotator(
        file,
        manager,
        isOnTheFly,
        LinterExternalAnnotator()
    )

    override fun buildVisitor(
        holder: ProblemsHolder,
        isOnTheFly: Boolean,
        session: LocalInspectionToolSession
    ): PsiElementVisitor = ExternalAnnotatorInspectionVisitor(holder, LinterExternalAnnotator(), isOnTheFly)

    override fun isSuppressedFor(element: PsiElement): Boolean = false

    override fun getBatchSuppressActions(element: PsiElement?): Array<SuppressQuickFix> = arrayOf()
}