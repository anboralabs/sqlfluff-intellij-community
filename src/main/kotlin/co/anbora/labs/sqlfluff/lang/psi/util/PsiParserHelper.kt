package co.anbora.labs.sqlfluff.lang.psi.util

import co.anbora.labs.sqlfluff.LinterConfigLanguage.LANGUAGE_DEMO_TEXT
import co.anbora.labs.sqlfluff.file.LinterFileType
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFileFactory

object PsiParserHelper {

    fun defaultLinterPsi(project: Project?): LinterConfigFile {
        return convertTextToPsi(project, "default.sqlfluff", LANGUAGE_DEMO_TEXT)
    }

    fun convertTextToPsi(
        project: Project?,
        fileName: String,
        text: String
    ) = PsiFileFactory.getInstance(project)
        .createFileFromText(
            fileName,
            LinterFileType,
            text
        ) as LinterConfigFile
}