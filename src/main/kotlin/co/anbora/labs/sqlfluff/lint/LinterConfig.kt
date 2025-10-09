package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.annotator.NO_PROBLEMS_FOUND
import co.anbora.labs.sqlfluff.ide.lang.psi.PsiFinderFlavor
import co.anbora.labs.sqlfluff.ide.quickFix.QuickFixFlavor
import co.anbora.labs.sqlfluff.ide.startup.InitConfigFiles.Companion.DEFAULT_CONFIG_PATH
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchain
import co.anbora.labs.sqlfluff.ide.utils.toPath
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import co.anbora.labs.sqlfluff.lang.psi.util.PsiParserHelper
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.StreamUtil
import com.intellij.openapi.vfs.VirtualFile
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.readText

enum class LinterConfig(protected val linter: Linter) {

    DISABLED(DisabledLinter) {
        override fun lint(
            state: LinterExternalAnnotator.State,
            configPath: String,
            toolchain: LinterToolchain,
            psiFinder: PsiFinderFlavor,
            quickFixer: QuickFixFlavor,
        ): LinterExternalAnnotator.Results = NO_PROBLEMS_FOUND

        override fun configPsiFile(project: Project?, path: String): LinterConfigFile? = null
    },
    GLOBAL(GlobalLinter) {
        override fun lint(
            state: LinterExternalAnnotator.State,
            configPath: String,
            toolchain: LinterToolchain,
            psiFinder: PsiFinderFlavor,
            quickFixer: QuickFixFlavor,
        ): LinterExternalAnnotator.Results {
            return linter.lint(state, DEFAULT_CONFIG_PATH.absolutePathString(), toolchain, psiFinder, quickFixer)
        }

        override fun configPsiFile(
            project: Project?,
            path: String
        ): LinterConfigFile = PsiParserHelper.defaultLinterPsi(project)
    },
    CUSTOM(CustomLinter) {
        override fun lint(
            state: LinterExternalAnnotator.State,
            configPath: String,
            toolchain: LinterToolchain,
            psiFinder: PsiFinderFlavor,
            quickFixer: QuickFixFlavor,
        ): LinterExternalAnnotator.Results = linter.lint(state, configPath, toolchain, psiFinder, quickFixer)

        override fun configPsiFile(
            project: Project?,
            path: String
        ): LinterConfigFile? {
            val filePath = path.toPath()
            if (filePath.exists() && !filePath.isDirectory()) {
                val text = filePath.readText()
                StreamUtil.convertSeparators(text)
                return PsiParserHelper.convertTextToPsi(project, ".sqlfluff", text)
            }
            return null
        }
    };

    abstract fun lint(
        state: LinterExternalAnnotator.State,
        configPath: String,
        toolchain: LinterToolchain,
        psiFinder: PsiFinderFlavor,
        quickFixer: QuickFixFlavor,
    ): LinterExternalAnnotator.Results

    abstract fun configPsiFile(project: Project?, path: String): LinterConfigFile?
}
