package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSchema

fun createUserStyleSchema(context: Context): UserStyleSchema {
    return UserStyleSchema(
        createBaseUserStyleSettings(context) +
        createFontUserStyleSettings(context) +
        createComplicationsUserStyleSettings(context)
    )
}