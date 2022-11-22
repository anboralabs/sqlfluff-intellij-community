package co.anbora.labs.sqlfluff.lint

import com.intellij.psi.PsiFile

const val SQL_LANG = "SQL"

fun PsiFile?.isSqlFileType(): Boolean {
    return this != null && this.fileType.name == SQL_LANG
}