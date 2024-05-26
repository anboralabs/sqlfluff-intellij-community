package co.anbora.labs.sqlfluff.ide.discovery

import com.intellij.openapi.extensions.ExtensionPointName
import java.nio.file.Path

abstract class LinterDiscoveryFlavor {

    abstract fun getPathCandidate(): Path?

    /**
     * Flavor is added to result in [getApplicableFlavors] if this method returns true.
     * @return whether this flavor is applicable.
     */
    protected open fun isApplicable(): Boolean = true

    companion object {
        private val EP_NAME: ExtensionPointName<LinterDiscoveryFlavor> =
            ExtensionPointName.create("co.anbora.labs.sqlfluff.discovery")

        fun getApplicableFlavors(): List<LinterDiscoveryFlavor> =
            EP_NAME.extensionList.filter { it.isApplicable() }
    }
}
