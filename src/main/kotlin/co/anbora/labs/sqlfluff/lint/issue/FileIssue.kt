package co.anbora.labs.sqlfluff.lint.issue

data class FileIssue(
    var filepath: String?,
    var violations: List<IssueItem>?
)