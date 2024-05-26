package co.anbora.labs.sqlfluff.ide.discovery.flavor

import co.anbora.labs.sqlfluff.ide.discovery.LinterDiscovery
import co.anbora.labs.sqlfluff.ide.discovery.LinterDiscoveryFlavor
import com.intellij.openapi.util.SystemInfo
import java.nio.file.Path

class LinterUnixDiscovery: LinterDiscoveryFlavor() {
    override fun getPathCandidate(): Path? {
        return LinterDiscovery.linterUnixPath
    }

    override fun isApplicable(): Boolean = SystemInfo.isUnix
}
