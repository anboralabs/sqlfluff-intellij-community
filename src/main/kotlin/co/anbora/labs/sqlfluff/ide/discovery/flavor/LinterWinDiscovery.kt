package co.anbora.labs.sqlfluff.ide.discovery.flavor

import co.anbora.labs.sqlfluff.ide.discovery.LinterDiscovery
import co.anbora.labs.sqlfluff.ide.discovery.LinterDiscoveryFlavor
import com.intellij.openapi.util.SystemInfo
import java.nio.file.Path

class LinterWinDiscovery: LinterDiscoveryFlavor() {
    override fun getPathCandidate(): Path? {
        return LinterDiscovery.linterWindowsPath
    }

    override fun isApplicable(): Boolean = SystemInfo.isWindows
}
