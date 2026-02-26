package co.anbora.labs.sqlfluff.ide

import com.intellij.DynamicBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
private const val BUNDLE = "co.anbora.labs.sqlfluff.messages"

object SqlfluffBundle : DynamicBundle(BUNDLE) {

    fun message(
        @PropertyKey(resourceBundle = BUNDLE) key: String,
        vararg params: Any
    ): String = getMessage(key, *params)
}
