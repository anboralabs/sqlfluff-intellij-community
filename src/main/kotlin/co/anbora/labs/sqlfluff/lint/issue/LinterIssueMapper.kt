package co.anbora.labs.sqlfluff.lint.issue

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.util.function.Function

object LinterIssueMapper: Function<String, List<FileIssue>> {

    private val MAPPER = jacksonObjectMapper()

    override fun apply(json: String): List<FileIssue> {
        return MAPPER.readValue(json, object : TypeReference<List<FileIssue>>() {})
    }
}
