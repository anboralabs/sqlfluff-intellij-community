package co.anbora.labs.sqlfluff.ide.settings.ui

import com.intellij.openapi.ui.getPresentablePath
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.SimpleTextAttributes
import javax.swing.JList

class LinterRenderer<T>: ColoredListCellRenderer<T>() {
    override fun customizeCellRenderer(
        list: JList<out T?>,
        value: T?,
        index: Int,
        selected: Boolean,
        hasFocus: Boolean
    ) {
        if (value != null) {
            append(getPresentablePath(value.toString()), SimpleTextAttributes.GRAYED_ATTRIBUTES)
        }
    }
}