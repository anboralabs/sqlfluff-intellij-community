package co.anbora.labs.sqlfluff.lang.psi

import co.anbora.labs.sqlfluff.LinterConfigLanguage
import co.anbora.labs.sqlfluff.file.LinterFileType
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class LinterConfigFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, LinterConfigLanguage) {
    override fun getFileType(): FileType = LinterFileType
}