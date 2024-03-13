package co.anbora.labs.sqlfluff.lint.issue

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class FileIssue(
    var filepath: String?,
    var violations: List<IssueItem>?
)
