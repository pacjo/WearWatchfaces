package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.HandsStyles

const val HANDS_STYLE_SETTING = "hands_style_setting"

const val SMOOTH_SECONDS_HAND_SETTING = "smooth_seconds_hand_setting"
const val SMOOTH_SECONDS_HAND_DEFAULT = true

/**
 * Creates [UserStyleSetting]s for hands configuration.
 */
fun createHandsUserStyleSettings(context: Context): List<UserStyleSetting> {
    val handsStyleSetting = UserStyleSetting.ListUserStyleSetting(
        UserStyleSetting.Id(HANDS_STYLE_SETTING),
        context.resources,
        R.string.hands_style_setting,
        R.string.hands_style_setting_description,
        null,
        HandsStyles.toOptionList(context),
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

    return listOf(
        handsStyleSetting,
        smoothSecondsHandSetting
    )
}