package co.anbora.labs.sqlfluff.lint

import com.intellij.psi.PsiFile
import com.intellij.sql.psi.SqlFile

fun PsiFile.isSqlFileType(): Boolean {
    return this is SqlFile
}