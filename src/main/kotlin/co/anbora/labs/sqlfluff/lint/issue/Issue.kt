package co.anbora.labs.sqlfluff.lint.issue

import co.anbora.labs.sqlfluff.lint.SQL_FLUFF
import com.intellij.lang.annotation.HighlightSeverity
import java.util.regex.Pattern

private val GENERAL_PATTERN = Pattern.compile("(\\w+\\d+)")

data class Issue(
    var filepath: String?,
    var lineNo: Int?,
    var linePos: Int?,
    var lineFilePos: Int?,
    var endLineNo: Int?,
    var endLinePos: Int?,
    var endFilePos: Int?,
    var code: String?,
    var description: String?,
    var name: String?
) {

    fun getMessage(): String {
        return "$SQL_FLUFF [$code]: $description"
    }

    fun getSeverity(): HighlightSeverity {
        return when {
            GENERAL_PATTERN.matcher(code.orEmpty()).matches() -> HighlightSeverity.WARNING
            else -> HighlightSeverity.ERROR
        }
    }
}