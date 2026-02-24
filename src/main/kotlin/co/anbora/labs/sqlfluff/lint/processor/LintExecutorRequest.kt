package co.anbora.labs.sqlfluff.lint.processor

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.lang.psi.PsiFinderFlavor
import co.anbora.labs.sqlfluff.ide.quickFix.QuickFixFlavor
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import java.nio.file.Path

data class LintExecutorRequest(
    val state: LinterExternalAnnotator.State,
    val workDirectory: Path?,
    val configPath: String,
    val toolchain: LinterToolchain,
    val psiFinder: PsiFinderFlavor,
    val quickFixer: QuickFixFlavor,
)
