package co.anbora.labs.sqlfluff.ide.discovery

import co.anbora.labs.sqlfluff.ide.settings.LinterConfigurationUtil
import co.anbora.labs.sqlfluff.ide.utils.toPathOrNull

object LinterDiscovery {

    val linterUnixPath by lazy {
        LinterConfigurationUtil.executeAndReturnOutput(
            "which",
            LinterConfigurationUtil.STANDARD_V_COMPILER
        ).toPathOrNull()
    }

    val linterWindowsPath by lazy {
        LinterConfigurationUtil.executeAndReturnOutput(
            "where",
            LinterConfigurationUtil.STANDARD_V_COMPILER
        ).toPathOrNull()
    }
}
