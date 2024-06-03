package co.anbora.labs.sqlfluff.ide.fs

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import java.nio.file.Path

interface LinterVirtualFile {

    fun isSqlFileType(): Boolean

    fun projectPath(): String?

    fun canonicalPath(): Path

    fun psiFile(): PsiFile

    fun document(): Document

    fun dialect(): String
}
