package co.anbora.labs.sqlfluff.ide.toolchain.flavor

import co.anbora.labs.sqlfluff.ide.toolchain.LinterToolchainFlavor
import co.anbora.labs.sqlfluff.ide.utils.toPathOrNull
import java.io.File
import java.nio.file.Path
import kotlin.io.path.isDirectory

class LinterSysPathToolchainFlavor : LinterToolchainFlavor() {
    override fun getHomePathCandidates(): Sequence<Path> {
        return System.getenv("PATH")
            .orEmpty()
            .split(File.pathSeparator)
            .asSequence()
            .filter { it.isNotEmpty() }
            .mapNotNull { it.toPathOrNull() }
            .filter { it.isDirectory() }
    }
}
