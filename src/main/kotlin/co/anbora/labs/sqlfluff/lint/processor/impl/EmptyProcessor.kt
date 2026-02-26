package co.anbora.labs.sqlfluff.lint.processor.impl

import co.anbora.labs.sqlfluff.ide.annotator.LinterExternalAnnotator
import co.anbora.labs.sqlfluff.ide.annotator.NO_PROBLEMS_FOUND
import co.anbora.labs.sqlfluff.lint.processor.ILintExecutor
import co.anbora.labs.sqlfluff.lint.processor.LintExecutorRequest
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class EmptyProcessor(val project: Project): ILintExecutor {
    override fun lint(request: LintExecutorRequest): LinterExternalAnnotator.Results = NO_PROBLEMS_FOUND
}