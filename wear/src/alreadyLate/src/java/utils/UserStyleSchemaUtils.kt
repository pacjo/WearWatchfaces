package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSchema
import kotlin.collections.plus

fun createUserStyleSchema(context: Context): UserStyleSchema {
    return UserStyleSchema(
        createBaseUserStyleSettings(context) +
        createHandsUserStyleSettings(context) +
        createBackgroundUserStyleSettings(context) +
        createComplicationsUserStyleSettings(context)
    )
}