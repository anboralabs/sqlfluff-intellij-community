package co.anbora.labs.sqlfluff.lint.api

import co.anbora.labs.sqlfluff.lint.checker.Problem
import co.anbora.labs.sqlfluff.lint.issue.IssueItem
import com.intellij.psi.PsiFile
import com.intellij.util.ThrowableRunnable
import java.util.*

class ProcessResultsThread(
    val suppressErrors: Boolean,
    val tabWidth: Int,
    val baseDir: String?,
    val errors: List<IssueItem>,
    val fileNamesToPsiFiles: Map<String, PsiFile>
): ThrowableRunnable<RuntimeException> {

    private val problems: Map<PsiFile, List<Problem>> = HashMap()

    override fun run() {
        val lineLengthCachesByFile: MutableMap<PsiFile, MutableList<Int>> = HashMap()

        for (event in errors) {
            /*val psiFile = fileNamesToPsiFiles[filenameFrom(event)]
            if (psiFile == null) {
                ProcessResultsThread.LOG.info(("Could not find mapping for file: " + event.getPath()).toString() + " in " + fileNamesToPsiFiles)
                return
            }

            var lineLengthCache = lineLengthCachesByFile[psiFile]
            if (lineLengthCache == null) {
                // we cache the offset of each line as it is created, so as to
                // avoid retreating ground we've already covered.
                lineLengthCache = ArrayList()
                lineLengthCache.add(0) // line 1 is offset 0

                lineLengthCachesByFile[psiFile] = lineLengthCache
            }

            processEvent(psiFile, lineLengthCache, event)*/
        }
    }

    fun getProblems(): Map<PsiFile, List<Problem>> {
        return Collections.unmodifiableMap(problems)
    }
}