package co.anbora.labs.sqlfluff.ide.fs

import co.anbora.labs.sqlfluff.lint.isSqlFileType
import com.intellij.openapi.editor.Document
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

class LinterVirtualFileImpl(
    private val document: Document,
    private val parent: VirtualFile,
    private val file: PsiFile
): LinterVirtualFile {

    private lateinit var tempFile: Path
    private lateinit var fileToDelete: File

    override fun createTempFile(): LinterVirtualFileImpl {
        val nioFile = parent.toNioPath()
        tempFile = Files.createTempFile(nioFile.parent, VIRTUAL_FILE_PREFIX, ".${parent.extension}")
        fileToDelete = tempFile.toFile().also {
            it.deleteOnExit()
        }
        Files.write(tempFile, document.text.toByteArray())
        return this
    }

    override fun projectPath(): String? = file.project.basePath

    override fun canonicalPath(): Path = tempFile

    override fun delete() {
        try {
            fileToDelete.delete()
            // Ignore IOException: if the file is already deleted, it doesn't matter
        } catch (_: IOException) { }
    }

    override fun psiFile(): PsiFile = file

    override fun document(): Document = document

    override fun isSqlFileType(): Boolean = file.isSqlFileType()

}
