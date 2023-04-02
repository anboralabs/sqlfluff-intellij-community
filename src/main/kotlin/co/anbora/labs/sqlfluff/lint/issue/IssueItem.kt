package co.anbora.labs.sqlfluff.lint.issue

import com.fasterxml.jackson.annotation.JsonProperty

data class IssueItem(
    @JsonProperty("line_no")
    var lineNo: Int?,
    @JsonProperty("line_pos")
    var linePos: Int?,
    @JsonProperty("code")
    var code: String?,
    @JsonProperty("description")
    var description: String?,
    @JsonProperty("name")
    var name: String?
)
