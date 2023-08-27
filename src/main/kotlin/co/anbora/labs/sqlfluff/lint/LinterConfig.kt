package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.fs.LinterVirtualFile

enum class LinterConfig(protected val linter: Linter) {

    DISABLED(DisabledLinter) {
        override fun lint(
            virtualFile: LinterVirtualFile
        ): List<LinterExternalAnnotator.Error> = linter.lint(virtualFile)
    },
    GLOBAL(GlobalLinter) {
        override fun lint(
            virtualFile: LinterVirtualFile
        ): List<LinterExternalAnnotator.Error> = linter.lint(virtualFile)
    },
    CUSTOM(CustomLinter) {
        override fun lint(
            virtualFile: LinterVirtualFile
        ): List<LinterExternalAnnotator.Error> = linter.lint(virtualFile)
    };

    abstract fun lint(
        virtualFile: LinterVirtualFile
    ): List<LinterExternalAnnotator.Error>

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
