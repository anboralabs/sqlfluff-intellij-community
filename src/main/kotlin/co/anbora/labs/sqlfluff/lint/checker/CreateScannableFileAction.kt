package co.anbora.labs.sqlfluff.lint.checker

import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import com.intellij.util.ThrowableRunnable
import java.io.IOException

class CreateScannableFileAction(
    val psiFile: Pair<PsiFile, Document>
): ThrowableRunnable<RuntimeException> {

    private var failure: IOException? = null

    private var file: ScannableFile? = null

    fun getFailure(): IOException? {
        return failure
    }

    fun getFile(): ScannableFile? {
        return file
    }

    override fun run() {
        try {
            file = ScannableFile(psiFile)
        } catch (e: IOException) {
            failure = e
        }
    }
}