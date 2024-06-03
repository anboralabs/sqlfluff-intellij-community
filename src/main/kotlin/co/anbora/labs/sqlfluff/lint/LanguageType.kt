package co.anbora.labs.sqlfluff.lint

import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainService
import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import com.intellij.psi.PsiFile

fun PsiFile?.isSqlFileType(): Boolean {
    val settings = LinterToolchainService.toolchainSettings
    val linter = settings.linter
    val configFile = linter.configPsiFile(this?.project, settings.configLocation)
    return this != null && configFile != null && "." + this.fileType.defaultExtension in configFile.extensions()
}

fun isSqlFileType(config: LinterConfigFile?, extension: String): Boolean {
    return config != null && extension in config.extensions()
}