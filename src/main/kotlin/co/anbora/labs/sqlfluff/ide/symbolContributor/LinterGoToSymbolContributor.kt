package co.anbora.labs.sqlfluff.ide.symbolContributor

import co.anbora.labs.sqlfluff.ide.stub.LinterPropertyKeyIndex
import com.intellij.navigation.ChooseByNameContributorEx
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.Processor
import com.intellij.util.indexing.FindSymbolParameters
import com.intellij.util.indexing.IdFilter
import ini4idea.lang.psi.IniProperty

class LinterGoToSymbolContributor: ChooseByNameContributorEx {
    override fun processNames(processor: Processor<in String>, scope: GlobalSearchScope, filter: IdFilter?) {
        StubIndex.getInstance().processAllKeys(LinterPropertyKeyIndex.KEY, processor, scope, filter)
    }

    override fun processElementsWithName(
        name: String,
        processor: Processor<in NavigationItem>,
        parameters: FindSymbolParameters
    ) {
        StubIndex.getInstance().processElements(
            LinterPropertyKeyIndex.KEY,
            StringUtil.toUpperCase(name),
            parameters.project,
            parameters.searchScope,
            parameters.idFilter,
            IniProperty::class.java,
            processor
        )
    }
}
