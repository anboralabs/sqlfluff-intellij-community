package co.anbora.labs.sqlfluff.ide.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ex.ExternalAnnotatorBatchInspection

class LinterInspection: LocalInspectionTool(), ExternalAnnotatorBatchInspection {

    override fun getShortName(): String = "Linter"
}
