package co.anbora.labs.sqlfluff.ide.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ex.ExternalAnnotatorBatchInspection

class LinterInspection: LocalInspectionTool(), ExternalAnnotatorBatchInspection {

    companion object {
        const val INSPECTION_SHORT_NAME = "SqlfluffLinter"
    }

    override fun getShortName(): String = INSPECTION_SHORT_NAME
}