package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.runner.SqlFluffLintRunner
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiFile
import java.nio.file.Files
import kotlin.io.path.pathString

private const val SQL_FLUFF = "sqlfluff"

object GlobalLinter: Linter() {

    override fun buildCommandLineArgs(
        python: String,
        lint: String,
        lintOptions: String,
        file: PsiFile,
        document: Document
    ): SqlFluffLintRunner.Param {
        val tempFile = Files.createTempFile(null, ".sql")
        Files.write(tempFile, document.text.toByteArray()) //hack trick because virtual file has the changes and real file no
        return SqlFluffLintRunner.Param(
            execPath = SQL_FLUFF,
            extraArgs = listOf(LINT_COMMAND, tempFile.pathString, *lintOptions.split(" ").toTypedArray())
        )
    }
}
