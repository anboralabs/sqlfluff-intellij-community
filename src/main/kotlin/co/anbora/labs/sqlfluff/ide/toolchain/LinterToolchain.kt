package co.anbora.labs.sqlfluff.ide.toolchain

import co.anbora.labs.sqlfluff.ide.settings.LinterConfigurationUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import java.nio.file.Path

interface LinterToolchain {
    fun name(): String
    fun version(): String
    fun rootDir(): VirtualFile?
    fun homePath(): String
    fun binPath(): String
    fun isValid(): Boolean

    companion object {

        fun fromState(state: LinterToolchainService.ToolchainState?): LinterToolchain {
            if (state == null) {
                return NULL
            }

            val homePath = state.toolchainLocation

            val virtualFileManager = VirtualFileManager.getInstance()
            val rootDir = virtualFileManager.findFileByNioPath(Path.of(homePath)) ?: return NULL

            return LinterLocalToolchain(state.toolchainVersion, rootDir)
        }

        fun fromPath(homePath: String): LinterToolchain {
            if (homePath == "") {
                return NULL
            }

            val virtualFileManager = VirtualFileManager.getInstance()
            val rootDir = virtualFileManager.findFileByNioPath(Path.of(homePath)) ?: return NULL
            return fromDirectory(rootDir)
        }

        private fun fromDirectory(rootDir: VirtualFile): LinterToolchain {
            val version = LinterConfigurationUtil.guessToolchainVersion(rootDir.path)
            return LinterLocalToolchain(version, rootDir)
        }

        val NULL = object : LinterToolchain {
            override fun name(): String = ""
            override fun version(): String = ""
            override fun rootDir(): VirtualFile? = null
            override fun homePath(): String = ""
            override fun binPath(): String = ""
            override fun isValid(): Boolean = false
        }
    }
}
