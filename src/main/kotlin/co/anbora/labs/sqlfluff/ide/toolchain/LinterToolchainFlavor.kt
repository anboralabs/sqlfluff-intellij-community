package co.anbora.labs.sqlfluff.ide.toolchain

import co.anbora.labs.sqlfluff.ide.settings.LinterConfigurationUtil
import com.intellij.openapi.extensions.ExtensionPointName
import java.nio.file.Path
import kotlin.io.path.isDirectory
import kotlin.io.path.isExecutable

abstract class LinterToolchainFlavor {

    fun suggestHomePaths(): Sequence<Path> = getHomePathCandidates().filter { isValidToolchainPath(it) }

    protected abstract fun getHomePathCandidates(): Sequence<Path>

    /**
     * Flavor is added to result in [getApplicableFlavors] if this method returns true.
     * @return whether this flavor is applicable.
     */
    protected open fun isApplicable(): Boolean = true

    /**
     * Checks if the path is the name of a V toolchain of this flavor.
     *
     * @param path path to check.
     * @return true if paths points to a valid home.
     */
    protected open fun isValidToolchainPath(path: Path): Boolean {
        return path.isDirectory()
                && path.resolve(LinterConfigurationUtil.STANDARD_V_COMPILER).isExecutable()
    }

    companion object {
        private val EP_NAME: ExtensionPointName<LinterToolchainFlavor> =
            ExtensionPointName.create("co.anbora.labs.sqlfluff.toolchain")

        fun getApplicableFlavors(): List<LinterToolchainFlavor> =
            EP_NAME.extensionList.filter { it.isApplicable() }

        fun getFlavor(path: Path): LinterToolchainFlavor? =
            getApplicableFlavors().find { flavor -> flavor.isValidToolchainPath(path) }
    }
}
