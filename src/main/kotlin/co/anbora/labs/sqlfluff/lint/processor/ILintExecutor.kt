package co.anbora.labs.sqlfluff.lint.processor

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator

interface ILintExecutor {

    fun lint(request: LintExecutorRequest): LinterExternalAnnotator.Results
}