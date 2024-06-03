package co.anbora.labs.sqlfluff.ide.fs

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import java.nio.file.Path

class LinterVirtualFileImpl(
    private val document: Document,
    private val file: PsiFile,
    private val dialect: String,
    private val isSql: Boolean
): LinterVirtualFile {

    override fun projectPath(): String? = file.project.basePath

    override fun canonicalPath(): Path = file.virtualFile.toNioPath()

    override fun psiFile(): PsiFile = file

    override fun document(): Document = document

    override fun dialect(): String = dialect

    override fun isSqlFileType(): Boolean = this.isSql

}
