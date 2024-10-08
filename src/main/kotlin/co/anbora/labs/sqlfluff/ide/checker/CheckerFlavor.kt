package co.anbora.labs.sqlfluff.ide.checker

import com.intellij.openapi.extensions.ExtensionPointName

abstract class CheckerFlavor {

    abstract fun check(): Boolean

    /**
     * Flavor is added to result in [getApplicableFlavors] if this method returns true.
     * @return whether this flavor is applicable.
     */
    protected open fun isApplicable(): Boolean = true

    companion object {
        private val EP_NAME: ExtensionPointName<CheckerFlavor> =
            ExtensionPointName.create("co.anbora.labs.sqlfluff.checker")

        fun getApplicableFlavors(): List<CheckerFlavor> =
            EP_NAME.extensionList.filter { it.isApplicable() }

        fun isSupported(): Boolean {
            return getApplicableFlavors().map { it.check() }
                .reduce { acc, b -> acc || b }
        }
    }
}
