package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.BackgroundStyles

const val BACKGROUND_STYLE_SETTING = "background_style_setting"

/**
 * Creates [UserStyleSetting]s for background configuration.
 */
fun createBackgroundUserStyleSettings(context: Context): List<UserStyleSetting> {
    val backgroundStyleSetting = UserStyleSetting.ListUserStyleSetting(
        UserStyleSetting.Id(BACKGROUND_STYLE_SETTING),
        context.resources,
        R.string.background_style_setting,
        R.string.background_style_setting_description,
        null,
        BackgroundStyles.toOptionList(context),
        WatchFaceLayer.ALL_WATCH_FACE_LAYERS    // TODO: u sure?
    )

    return listOf(backgroundStyleSetting)
}