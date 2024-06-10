package co.anbora.labs.sqlfluff.lint.api

import co.anbora.labs.sqlfluff.ide.lang.psi.PsiFinderFlavor
import co.anbora.labs.sqlfluff.lint.checker.Problem
import co.anbora.labs.sqlfluff.lint.checker.PsiProblem
import co.anbora.labs.sqlfluff.lint.checker.TextRangeProblem
import co.anbora.labs.sqlfluff.lint.issue.Issue
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiInvalidElementAccessException
import com.intellij.util.ThrowableRunnable
import java.util.*

class ProcessResultsThread(
    val psiFinder: PsiFinderFlavor,
    val suppressErrors: Boolean,
    val tabWidth: Int,
    val baseDir: String?,
    val errors: List<Issue>,
    val fileNamesToPsiFiles: Map<String, PsiFile>
): ThrowableRunnable<RuntimeException> {

    private val log = Logger.getInstance(
        ProcessResultsThread::class.java
    )

    private val problems: MutableMap<PsiFile, MutableList<Problem>> = HashMap()

    override fun run() {
        val lineLengthCachesByFile: MutableMap<PsiFile, MutableList<Int>> = HashMap()

        for (event in errors) {
            val psiFile = fileNamesToPsiFiles[event.filepath]
            if (psiFile == null) {
                log.info(("Could not find mapping for file: " + event.filepath) + " in " + fileNamesToPsiFiles)
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

            processEvent(psiFile, lineLengthCache, event)
        }
    }

    private fun processEvent(psiFile: PsiFile, lineLengthCache: List<Int>, event: Issue) {
        val element = psiFinder.findElement(psiFile, psiFile.fileDocument, event)

        val (textRange, psiElement) = element

        if (psiElement != null) {
            addProblemTo(psiElement, psiFile, event)
        } else if (textRange != null) {
            val afterEndOfLine = textRange.startOffset == textRange.endOffset

            addProblemTo(textRange, psiFile, event, afterEndOfLine)
        }
    }

    private fun addProblemTo(psiElement: PsiElement, psiFile: PsiFile, event: Issue) {
        try {
            addProblem(
                psiFile,
                PsiProblem(
                    event.getMessage(),
                    psiElement,
                    event.getSeverity(),
                    null
                )
            )
        } catch (ex: PsiInvalidElementAccessException) {
            log.warn("Element access failed", ex)
        }
    }

    private fun addProblemTo(textRange: TextRange, psiFile: PsiFile, event: Issue, afterEndOfLine: Boolean) {
        try {
            addProblem(
                psiFile,
                TextRangeProblem(
                    event.getMessage(),
                    textRange,
                    event.getSeverity(),
                    afterEndOfLine
                )
            )
        } catch (ex: PsiInvalidElementAccessException) {
            log.warn("Element access failed", ex)
        }
    }

    fun getProblems(): Map<PsiFile, List<Problem>> {
        return Collections.unmodifiableMap(problems)
    }

    private fun addProblem(psiFile: PsiFile, problem: Problem) {
        var problemsForFile: MutableList<Problem>? = problems[psiFile]
        if (problemsForFile == null) {
            problemsForFile = ArrayList()
            problems[psiFile] = problemsForFile
        }

        problemsForFile.add(problem)
    }
}