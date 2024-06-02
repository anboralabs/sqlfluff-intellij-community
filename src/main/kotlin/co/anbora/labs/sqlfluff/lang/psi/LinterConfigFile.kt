package co.anbora.labs.sqlfluff.lang.psi

import co.anbora.labs.sqlfluff.LinterConfigLanguage
import co.anbora.labs.sqlfluff.file.LinterFileType
import co.anbora.labs.sqlfluff.ide.icons.LinterIcons
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import ini4idea.lang.psi.IniPsiElement
import javax.swing.Icon

class LinterConfigFile(
    viewProvider: FileViewProvider
): PsiFileBase(viewProvider, LinterConfigLanguage), IniPsiElement {
    override fun getFileType(): FileType = LinterFileType

    override fun getPresentation(): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String {
                return this@LinterConfigFile.name
            }

            override fun getLocationString(): String? {
                return getVirtualFile()?.presentableUrl
            }

            override fun getIcon(unused: Boolean): Icon = LinterIcons.SQL_FLUFF_16
        }
    }
}