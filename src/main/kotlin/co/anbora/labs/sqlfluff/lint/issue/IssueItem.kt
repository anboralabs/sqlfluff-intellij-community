package co.anbora.labs.sqlfluff.lint.issue

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class IssueItem(
    @JsonProperty("line_no")
    @JsonAlias("start_line_no")
    var lineNo: Int?,
    @JsonProperty("line_pos")
    @JsonAlias("start_line_pos")
    var linePos: Int?,
    @JsonAlias("start_file_pos")
    var lineFilePos: Int?,
    @JsonAlias("end_line_no")
    var endLineNo: Int?,
    @JsonAlias("end_line_pos")
    var endLinePos: Int?,
    @JsonAlias("end_file_pos")
    var endFilePos: Int?,
    @JsonProperty("code")
    var code: String?,
    @JsonProperty("description")
    var description: String?,
    @JsonProperty("name")
    var name: String?
)
