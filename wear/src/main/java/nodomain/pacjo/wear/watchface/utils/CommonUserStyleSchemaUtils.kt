package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyle

const val COLOR_STYLE_SETTING = "color_style_setting"

/**
 * Creates a base user style schema.
 * It contains common settings shared between multiple watchfaces. Currently:
 *      - color styles
 */
fun createBaseUserStyleSettings(context: Context): List<UserStyleSetting> {
    val colorStyleSetting = UserStyleSetting.ListUserStyleSetting(
        UserStyleSetting.Id(COLOR_STYLE_SETTING),
        context.resources,
        R.string.colors_style_setting,
        R.string.colors_style_setting_description,
        null,
        ColorStyle.toOptionList(context),
        WatchFaceLayer.ALL_WATCH_FACE_LAYERS
    )

    return listOf(
        colorStyleSetting
    )
}