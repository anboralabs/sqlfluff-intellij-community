package co.anbora.labs.sqlfluff.ide.toolchain.flavor

import co.anbora.labs.sqlfluff.ide.discovery.LinterDiscovery
import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainFlavor
import com.intellij.openapi.util.SystemInfo
import java.nio.file.Path

class LinterWinPathToolchainFlavor: LinterToolchainFlavor() {
    override fun getHomePathCandidates(): Sequence<Path> {
        val path = LinterDiscovery.linterWindowsPath
        if (path != null) {
            return listOf(path.parent).asSequence()
        }
        return emptySequence()
    }

    override fun isApplicable(): Boolean = SystemInfo.isWindows
}