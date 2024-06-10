package co.anbora.labs.sqlfluff.lint.checker

import co.anbora.labs.sqlfluff.ide.lang.psi.PsiFinderFlavor
import co.anbora.labs.sqlfluff.ide.quickFix.QuickFixFlavor
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import co.anbora.labs.sqlfluff.lint.api.LinterRunner
import co.anbora.labs.sqlfluff.lint.api.ProcessResultsThread
import co.anbora.labs.sqlfluff.lint.exception.LinterException
import co.anbora.labs.sqlfluff.lint.issue.Issue
import co.anbora.labs.sqlfluff.lint.issue.IssueItem
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileVisitor
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import java.io.InterruptedIOException
import java.util.concurrent.Callable

class ScanFiles(
    val project: Project,
    val configPath: String,
    val toolchain: LinterToolchain,
    val psiFinder: PsiFinderFlavor,
    val quickFixer: QuickFixFlavor,
    virtualFiles: List<VirtualFile>
): Callable<Map<PsiFile, List<Problem>>> {

    private val log = Logger.getInstance(
        ScanFiles::class.java
    )

    private val files: List<PsiFile> = findAllFilesFor(virtualFiles)

    private fun findAllFilesFor(virtualFiles: List<VirtualFile>): List<PsiFile> {
        val childFiles = mutableListOf<PsiFile>()
        val psiManager = PsiManager.getInstance(project)
        for (virtualFile in virtualFiles) {
            childFiles.addAll(buildFilesList(psiManager, virtualFile))
        }
        return childFiles
    }

    private fun buildFilesList(psiManager: PsiManager, virtualFile: VirtualFile): List<PsiFile> {
        val allChildFiles = mutableListOf<PsiFile>()
        ApplicationManager.getApplication().runReadAction {
            val visitor = FindChildFiles(virtualFile, psiManager)
            VfsUtilCore.visitChildrenRecursively(virtualFile, visitor)
            allChildFiles.addAll(visitor.locatedFiles)
        }
        return allChildFiles
    }

    private inner class FindChildFiles(
        val virtualFile: VirtualFile,
        val psiManager: PsiManager
    ): VirtualFileVisitor<PsiFile>() {

        val locatedFiles = mutableListOf<PsiFile>()

        override fun visitFile(file: VirtualFile): Boolean {
            if (!file.isDirectory) {
                val psiFile = psiManager.findFile(virtualFile)
                if (psiFile != null) {
                    locatedFiles.add(psiFile)
                }
            }
            return true
        }
    }

    override fun call(): Map<PsiFile, List<Problem>> {
        try {
            return scanCompletedSuccessfully(checkFiles(files.toSet()))
        } catch (ex: InterruptedIOException) {
            log.debug("Scan cancelled by IDE", ex)
            return scanCompletedSuccessfully(emptyMap())
        } catch (ex: InterruptedException) {
            log.debug("Scan cancelled by IDE", ex)
            return scanCompletedSuccessfully(emptyMap())
        } catch (ex: LinterException) {
            log.warn("An error occurred while scanning a file.", ex)
            return scanFailedWithError(ex)
        } catch (ex: Throwable) {
            log.warn("An error occurred while scanning a file.", ex)
            return scanFailedWithError(LinterException("An error occurred while scanning a file.", ex))
        }
    }

    private fun scanFailedWithError(e: LinterException): Map<PsiFile, List<Problem>> {
        // Notifications.showException(project, e)

        return emptyMap()
    }

    @Throws(InterruptedIOException::class, InterruptedException::class)
    private fun checkFiles(filesToScan: Set<PsiFile>): Map<PsiFile, List<Problem>> {
        val scannableFiles = mutableListOf<ScannableFile>()
        try {
            scannableFiles.addAll(ScannableFile.createAndValidate(filesToScan))
            return scan(scannableFiles)
        } finally {
            scannableFiles.forEach(ScannableFile::deleteIfRequired)
        }
    }

    @Throws(InterruptedIOException::class, InterruptedException::class)
    private fun scan(filesToScan: List<ScannableFile>): Map<PsiFile, List<Problem>> {
        val fileNamesToPsiFiles: Map<String, PsiFile> = mapFilesToElements(filesToScan)
        val errors: List<Issue> = LinterRunner.lint(project, configPath, toolchain, fileNamesToPsiFiles.keys)
        val baseDir: String? = project.basePath
        val tabWidth = 4
        val findThread = ProcessResultsThread(
            psiFinder, quickFixer,
            false, tabWidth, baseDir,
            errors, fileNamesToPsiFiles
        )

        ReadAction.run(findThread)
        return findThread.getProblems()
    }

    private fun mapFilesToElements(filesToScan: List<ScannableFile>): Map<String, PsiFile> {
        val filePathsToElements: MutableMap<String, PsiFile> = HashMap()
        for (scannableFile in filesToScan) {
            filePathsToElements[scannableFile.getAbsolutePath()] = scannableFile.psiFile
        }
        return filePathsToElements
    }

    private fun scanCompletedSuccessfully(filesToProblems: Map<PsiFile, List<Problem>>): Map<PsiFile, List<Problem>> {
        return filesToProblems
    }
}