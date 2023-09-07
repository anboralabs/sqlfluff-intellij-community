package co.anbora.labs.sqlfluff.ide.fs

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import java.nio.file.Path

const val VIRTUAL_FILE_PREFIX = "__sqlfluff_tmp_"

interface LinterVirtualFile {

    fun isSqlFileType(): Boolean

    fun createTempFile(): LinterVirtualFile

    fun projectPath(): String?

    fun canonicalPath(): Path

    fun delete()

    fun psiFile(): PsiFile

    fun document(): Document
}
