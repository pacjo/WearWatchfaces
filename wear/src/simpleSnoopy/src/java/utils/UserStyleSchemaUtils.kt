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

const val COLOR_STYLE_SETTING = "color_style_setting"
const val HANDS_STYLE_SETTING = "hands_style_setting"
const val BACKGROUND_STYLE_SETTING = "background_style_setting"
const val SMOOTH_SECONDS_HAND_SETTING = "smooth_seconds_hand_setting"

fun createUserStyleSchema(context: Context): UserStyleSchema {
    val colorStyleSetting =
        UserStyleSetting.ListUserStyleSetting(
            UserStyleSetting.Id(COLOR_STYLE_SETTING),
            context.resources,
            R.string.colors_style_setting,
            R.string.colors_style_setting_description,
            null,
            ColorStyle.toOptionList(context),
            WatchFaceLayer.ALL_WATCH_FACE_LAYERS
        )

    val handsStyleSetting =
        UserStyleSetting.ListUserStyleSetting(
            UserStyleSetting.Id(HANDS_STYLE_SETTING),
            context.resources,
            R.string.hands_style_setting,
            R.string.hands_style_setting_description,
            null,
            HandsStyles.toOptionList(context),
            WatchFaceLayer.ALL_WATCH_FACE_LAYERS    // TODO: u sure?
        )

    val backgroundStyleSetting =
        UserStyleSetting.ListUserStyleSetting(
            UserStyleSetting.Id(BACKGROUND_STYLE_SETTING),
            context.resources,
            R.string.background_style_setting,
            R.string.background_style_setting_description,
            null,
            BackgroundStyles.toOptionList(context),
            WatchFaceLayer.ALL_WATCH_FACE_LAYERS    // TODO: u sure?
        )

    val smoothSecondsHandSetting = UserStyleSetting.BooleanUserStyleSetting(
        UserStyleSetting.Id(SMOOTH_SECONDS_HAND_SETTING),
        context.resources,
        R.string.misc_smooth_seconds_hand,
        R.string.misc_smooth_seconds_hand_description,
        null,
        listOf(WatchFaceLayer.COMPLICATIONS),
        SMOOTH_SECONDS_HAND_DEFAULT
    )

    return UserStyleSchema(
        listOf(
            colorStyleSetting,
            handsStyleSetting,
            backgroundStyleSetting,
            smoothSecondsHandSetting
        )
    )
}