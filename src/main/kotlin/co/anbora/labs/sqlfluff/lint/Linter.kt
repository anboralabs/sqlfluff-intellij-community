package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.annotator.NO_PROBLEMS_FOUND
import co.anbora.labs.sqlfluff.ide.lang.psi.PsiFinderFlavor
import co.anbora.labs.sqlfluff.ide.quickFix.QuickFixFlavor
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import co.anbora.labs.sqlfluff.lint.checker.ScanFiles
import com.intellij.openapi.diagnostic.Logger
import java.nio.file.Path

const val SQL_FLUFF = "sqlfluff"
const val VIRTUAL_FILE_PREFIX = "__sqlfluff_tmp_"

sealed class Linter {

    protected val log: Logger = Logger.getInstance(
        Linter::class.java
    )

    fun lint(
        state: LinterExternalAnnotator.State,
        workDirectory: Path?,
        configPath: String,
        toolchain: LinterToolchain,
        psiFinder: PsiFinderFlavor,
        quickFixer: QuickFixFlavor,
    ): LinterExternalAnnotator.Results {

        log.info("sqlfluff linter executing")

        val psiFile = state.psiWithDocument.first
        val project = psiFile.project

        val scanFiles = ScanFiles(
            project, workDirectory, configPath, toolchain, psiFinder, quickFixer, listOf(state.psiWithDocument)
        )

        val map = scanFiles.call()

        if (map.isEmpty()) {
            return NO_PROBLEMS_FOUND
        }

        return LinterExternalAnnotator.Results(map[psiFile] ?: emptyList())
    }
}
