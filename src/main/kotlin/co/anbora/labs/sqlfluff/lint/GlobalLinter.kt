package co.anbora.labs.sqlfluff.lint

import com.intellij.psi.PsiFile

private const val SQL_FLUFF = "sqlfluff"

object GlobalLinter: Linter() {

    override fun buildCommandLineArgs(
        python: String,
        lint: String,
        lintOptions: String,
        file: PsiFile
    ): List<String> {
        val command = "$SQL_FLUFF lint ${file.virtualFile.canonicalPath} $lintOptions"
        return command.split(" ")
    }
}