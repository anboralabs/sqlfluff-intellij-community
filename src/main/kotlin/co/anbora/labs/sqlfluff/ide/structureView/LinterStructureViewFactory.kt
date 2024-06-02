package co.anbora.labs.sqlfluff.ide.structureView

import co.anbora.labs.sqlfluff.lang.psi.LinterConfigFile
import com.intellij.ide.structureView.*
import com.intellij.ide.structureView.StructureViewModel.ElementInfoProvider
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.lang.PsiStructureViewFactory
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.Editor
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiFile
import ini4idea.lang.psi.IniProperty
import ini4idea.lang.psi.IniSection
import java.util.*

class LinterStructureViewFactory: PsiStructureViewFactory {
    override fun getStructureViewBuilder(psiFile: PsiFile): StructureViewBuilder? {
        if (psiFile is LinterConfigFile) {
            return object : TreeBasedStructureViewBuilder() {
                override fun createStructureViewModel(editor: Editor?): StructureViewModel {
                    return Model(psiFile, editor)
                }

                override fun isRootNodeShown(): Boolean {
                    return false
                }
            }
        }
        return null
    }

    abstract class IniElement<T : NavigatablePsiElement>(
        protected val myElement: T
    ) : StructureViewTreeElement {
        override fun getValue(): Any {
            return this.myElement
        }

        override fun navigate(requestFocus: Boolean) {
            myElement.navigate(requestFocus)
        }

        override fun canNavigate(): Boolean {
            return myElement.canNavigate()
        }

        override fun canNavigateToSource(): Boolean {
            return myElement.canNavigateToSource()
        }

        override fun getPresentation(): ItemPresentation {
           return Objects.requireNonNull(
                myElement.presentation
            ) as ItemPresentation
        }
    }

    class FileElement(
        element: LinterConfigFile
    ) : IniElement<LinterConfigFile>(element) {

        override fun getChildren(): Array<TreeElement> {
            val result: ArrayList<TreeElement> = ArrayList<TreeElement>()
            val psiElements = (myElement as LinterConfigFile?)!!.children
            val size = psiElements.size

            for (var4 in 0 until size) {
                val child = psiElements[var4]
                if (child is IniSection) {
                    if (child.isNamed) {
                        result.add(SectionElement(child))
                    } else {
                        val var6 = child.getChildren()
                        val var7 = var6.size

                        for (var8 in 0 until var7) {
                            val grandchild = var6[var8]
                            if (grandchild is IniProperty) {
                                result.add(PropertyElement(grandchild))
                            }
                        }
                    }
                }
            }

            return result.toArray(TreeElement.EMPTY_ARRAY) as Array<TreeElement>
        }
    }

    class SectionElement (element: IniSection) :
        IniElement<IniSection>(element) {

        override fun getChildren(): Array<TreeElement> {
            val result: ArrayList<TreeElement> = ArrayList<TreeElement>()
            val psiElements = (myElement as IniSection?)!!.children
            val var3 = psiElements.size

            for (var4 in 0 until var3) {
                val child = psiElements[var4]
                if (child is IniProperty) {
                    result.add(PropertyElement(child))
                }
            }

            return result.toArray(TreeElement.EMPTY_ARRAY) as Array<TreeElement>
        }
    }

    class PropertyElement(element: IniProperty) : IniElement<IniProperty>(element) {

        override fun getChildren(): Array<TreeElement> = TreeElement.EMPTY_ARRAY
    }

    class Model(
        psiFile: LinterConfigFile,
        editor: Editor?
    ): StructureViewModelBase(psiFile, editor, FileElement(psiFile)), ElementInfoProvider {

        private val SUITABLE_CLASSES = arrayOf<Class<*>>(
            IniSection::class.java,
            IniProperty::class.java,
            LinterConfigFile::class.java
        )

        init {
            this.withSuitableClasses(*SUITABLE_CLASSES)
        }

        override fun isAlwaysShowsPlus(element: StructureViewTreeElement): Boolean {
            return false
        }

        override fun isAlwaysLeaf(element: StructureViewTreeElement): Boolean {
            val psi = element.value
            return psi is IniProperty
        }
    }
}