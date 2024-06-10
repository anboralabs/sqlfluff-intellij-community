package co.anbora.labs.sqlfluff.lint.issue

import java.util.function.Function

object IssueMapper: Function<FileIssue, List<Issue>> {
    override fun apply(fileIssue: FileIssue): List<Issue> {

        val violations = fileIssue.violations ?: return emptyList()

        return violations.map {
            Issue(
                fileIssue.filepath,
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