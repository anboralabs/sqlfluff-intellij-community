package co.anbora.labs.sqlfluff.ide.toolchain

import co.anbora.labs.sqlfluff.ide.settings.LinterConfigurationUtil
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import kotlin.io.path.isExecutable

class LinterLocalToolchain(
    private val version: String,
    private val rootDir: VirtualFile,
): LinterToolchain {
    private val homePath = rootDir.path
    private val executable = rootDir.findChild(LinterConfigurationUtil.STANDARD_V_COMPILER)

    override fun name(): String = version

    override fun version(): String = version

    override fun rootDir(): VirtualFile = rootDir

    override fun homePath(): String = homePath

    override fun binPath(): String = executable?.toNioPath()?.toString() ?: ""

    override fun isValid(): Boolean {
        return isValidDir(rootDir) && isValidExecutable(executable)
    }

    private fun isValidDir(dir: VirtualFile?): Boolean {
        return dir != null && dir.isValid
                && dir.isInLocalFileSystem && dir.isDirectory
    }

    private fun isValidExecutable(executable: VirtualFile?): Boolean {
        return executable != null && executable.isValid
                && executable.isInLocalFileSystem && executable.toNioPath().isExecutable()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LinterLocalToolchain

        return FileUtil.comparePaths(other.homePath(), homePath()) == 0
    }

    override fun hashCode(): Int = homePath.hashCode()
}
