package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.Fonts

const val FONT_SETTING = "font_setting"

const val USE_CUSTOM_FONT_FOR_COMPLICATIONS_SETTING = "use_custom_font_for_complications"
const val USE_CUSTOM_FONT_FOR_COMPLICATIONS_DEFAULT = true

/**
 * Creates [UserStyleSetting]s for font configuration.
 */
fun createFontUserStyleSettings(context: Context): List<UserStyleSetting> {
    val fontSetting = UserStyleSetting.ListUserStyleSetting(
        UserStyleSetting.Id(FONT_SETTING),
        context.resources,
        R.string.font_setting,
        R.string.font_setting_description,
        null,
        Fonts.toOptionList(context),
        listOf(WatchFaceLayer.BASE)
    )

    val useCustomFontForComplications = UserStyleSetting.BooleanUserStyleSetting(
        UserStyleSetting.Id(USE_CUSTOM_FONT_FOR_COMPLICATIONS_SETTING),
        context.resources,
        R.string.font_complications,
        R.string.font_complications_description,
        null,       // TODO: hey, let's maybe use those icons?
        listOf(WatchFaceLayer.COMPLICATIONS),
        USE_CUSTOM_FONT_FOR_COMPLICATIONS_DEFAULT
    )

    return listOf(
        fontSetting,
        useCustomFontForComplications
    )
}