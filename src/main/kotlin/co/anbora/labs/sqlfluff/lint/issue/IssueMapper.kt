package co.anbora.labs.sqlfluff.lint.issue

import co.anbora.labs.sqlfluff.lint.api.ScanParams
import java.util.function.BiFunction

object IssueMapper: BiFunction<FileIssue, ScanParams, List<Issue>> {
    override fun apply(fileIssue: FileIssue, params: ScanParams): List<Issue> {

        val violations = fileIssue.violations ?: return emptyList()

        return violations.map {
            Issue(
                params.filePath,
                it.lineNo,
                it.linePos,
                it.lineFilePos,
                it.endLineNo,
                it.endLinePos,
                it.endFilePos,
                it.code,
                it.description,
                it.name
            )
        }
    }
}
