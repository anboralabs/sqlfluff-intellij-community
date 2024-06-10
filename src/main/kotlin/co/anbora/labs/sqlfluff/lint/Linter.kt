package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.annotator.NO_PROBLEMS_FOUND
import co.anbora.labs.sqlfluff.ide.lang.psi.PsiFinderFlavor
import co.anbora.labs.sqlfluff.ide.quickFix.QuickFixFlavor
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import co.anbora.labs.sqlfluff.lint.checker.ScanFiles
import com.intellij.openapi.diagnostic.Logger

const val SQL_FLUFF = "sqlfluff"
const val VIRTUAL_FILE_PREFIX = "__sqlfluff_tmp_"

sealed class Linter {

    protected val log: Logger = Logger.getInstance(
        Linter::class.java
    )

    fun lint(
        state: LinterExternalAnnotator.State,
        configPath: String,
        toolchain: LinterToolchain,
        psiFinder: PsiFinderFlavor,
        quickFixer: QuickFixFlavor,
    ): LinterExternalAnnotator.Results {

        log.info("sqlfluff linter executing")

        val project = state.psiFile.project
        val virtualFile = state.psiFile.virtualFile
        val psiFile = state.psiFile

        val scanFiles = ScanFiles(
            project, configPath, toolchain, psiFinder, quickFixer, listOf(virtualFile)
        )

        val map = scanFiles.call()

        if (map.isEmpty()) {
            return NO_PROBLEMS_FOUND
        }

        return LinterExternalAnnotator.Results(map[psiFile] ?: emptyList())
    }
}
