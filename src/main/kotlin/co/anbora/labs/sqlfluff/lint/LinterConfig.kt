package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.fs.LinterVirtualFile
import co.anbora.labs.sqlfluff.ide.utils.toPath
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import co.anbora.labs.sqlfluff.lang.psi.util.PsiParserHelper
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.StreamUtil
import kotlin.io.path.exists
import kotlin.io.path.readText

enum class LinterConfig(protected val linter: Linter) {

    DISABLED(DisabledLinter) {
        override fun lint(
            virtualFile: LinterVirtualFile
        ): List<LinterExternalAnnotator.Error> = linter.lint(virtualFile)

        override fun configPsiFile(project: Project?, path: String): LinterConfigFile? = null
    },
    GLOBAL(GlobalLinter) {
        override fun lint(
            virtualFile: LinterVirtualFile
        ): List<LinterExternalAnnotator.Error> = linter.lint(virtualFile)

        override fun configPsiFile(
            project: Project?,
            path: String
        ): LinterConfigFile = PsiParserHelper.defaultLinterPsi(project)
    },
    CUSTOM(CustomLinter) {
        override fun lint(
            virtualFile: LinterVirtualFile
        ): List<LinterExternalAnnotator.Error> = linter.lint(virtualFile)

        override fun configPsiFile(
            project: Project?,
            path: String
        ): LinterConfigFile? {
            val filePath = path.toPath()
            if (filePath.exists()) {
                val text = filePath.readText()
                StreamUtil.convertSeparators(text)
                return PsiParserHelper.convertTextToPsi(project, ".sqlfluff", text)
            }
            return null
        }
    };

    abstract fun lint(
        virtualFile: LinterVirtualFile
    ): List<LinterExternalAnnotator.Error>

    abstract fun configPsiFile(project: Project?, path: String): LinterConfigFile?

    companion object {
        fun getOrDefault(enum: String): LinterConfig {
            return try {
                LinterConfig.valueOf(enum)
            } catch (ex: Exception) {
                DISABLED
            }
        }
    }
}
