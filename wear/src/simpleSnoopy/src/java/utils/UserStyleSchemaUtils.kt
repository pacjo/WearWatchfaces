package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSchema
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.BackgroundStyles
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyle
import nodomain.pacjo.wear.watchface.data.watchface.HandsStyles
import nodomain.pacjo.wear.watchface.data.watchface.SMOOTH_SECONDS_HAND_DEFAULT

fun createUserStyleSchema(context: Context): UserStyleSchema {
    return UserStyleSchema(
        createBaseUserStyleSettings(context) +
        createBackgroundUserStyleSettings(context) +
        createHandsUserStyleSettings(context)
    )
}