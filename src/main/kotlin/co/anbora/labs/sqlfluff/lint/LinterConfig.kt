package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.startup.InitConfigFiles.Companion.DEFAULT_CONFIG_PATH
import co.anbora.labs.sqlfluff.ide.utils.toPath
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import co.anbora.labs.sqlfluff.lang.psi.util.PsiParserHelper
import co.anbora.labs.sqlfluff.lint.processor.ILintExecutor
import co.anbora.labs.sqlfluff.lint.processor.impl.EmptyProcessor
import co.anbora.labs.sqlfluff.lint.processor.impl.FileSystemExecutor
import co.anbora.labs.sqlfluff.lint.processor.impl.StdInExecutor
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.StreamUtil
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.readText

enum class LinterConfig {

    DISABLED {
        override fun chooseExecutor(project: Project, executeOnSave: Boolean): ILintExecutor {
            return project.service<EmptyProcessor>()
        }

        override fun configPsiFile(project: Project?, path: String): LinterConfigFile? = null

        override fun workDirectory(project: Project?, configPath: String): Path? = null

        override fun configPath(configPath: String): String = configPath
    },
    GLOBAL {
        override fun chooseExecutor(
            project: Project,
            executeOnSave: Boolean
        ): ILintExecutor {
            if (executeOnSave) {
                return project.service<FileSystemExecutor>()
            }
            return project.service<StdInExecutor>()
        }

        override fun configPsiFile(
            project: Project?,
            path: String
        ): LinterConfigFile = PsiParserHelper.defaultLinterPsi(project)

        override fun workDirectory(project: Project?, configPath: String): Path? = project?.basePath?.toPath()

        override fun configPath(configPath: String): String = DEFAULT_CONFIG_PATH.absolutePathString()
    },
    CUSTOM {
        override fun chooseExecutor(
            project: Project,
            executeOnSave: Boolean
        ): ILintExecutor {
            if (executeOnSave) {
                return project.service<FileSystemExecutor>()
            }
            return project.service<StdInExecutor>()
        }

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

        override fun workDirectory(project: Project?, configPath: String): Path? = configPath.toPath().parent

        override fun configPath(configPath: String): String = configPath
    };

    abstract fun chooseExecutor(project: Project, executeOnSave: Boolean): ILintExecutor

    abstract fun configPsiFile(project: Project?, path: String): LinterConfigFile?

    abstract fun workDirectory(project: Project?, configPath: String): Path?

    abstract fun configPath(configPath: String): String
}
