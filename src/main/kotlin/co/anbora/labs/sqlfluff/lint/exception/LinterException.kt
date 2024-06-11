package co.anbora.labs.sqlfluff.lint.exception

data class LinterException(
    private val _message: String,
    private val _cause: Throwable? = null
): RuntimeException(_message, _cause)
