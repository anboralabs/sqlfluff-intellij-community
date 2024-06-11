package co.anbora.labs.sqlfluff.ide.stub

import com.intellij.psi.stubs.StringStubIndexExtension
import com.intellij.psi.stubs.StubIndexKey
import ini4idea.ide.IniPropertyKeyIndex
import ini4idea.lang.psi.IniProperty

class LinterPropertyKeyIndex: StringStubIndexExtension<IniProperty>() {

    companion object {
        val KEY: StubIndexKey<String, IniProperty> = StubIndexKey.createIndexKey("sqlfluff.property.key")
        val ourInstance: IniPropertyKeyIndex = IniPropertyKeyIndex()
    }

    override fun getKey(): StubIndexKey<String, IniProperty> = KEY
}
