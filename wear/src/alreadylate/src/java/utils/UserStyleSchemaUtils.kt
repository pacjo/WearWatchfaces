package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSchema
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds
import nodomain.pacjo.wear.watchface.data.watchface.DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT
import nodomain.pacjo.wear.watchface.data.watchface.SMOOTH_SECONDS_HAND_DEFAULT

// Keys to matched content in the  the user style settings. We listen for changes to these
// values in the renderer and if new, we will update the database and update the watch face
// being rendered.
const val COLOR_STYLE_SETTING = "color_style_setting"
const val DRAW_COMPLICATIONS_IN_AMBIENT_SETTING = "draw_complications_in_ambient_setting"
const val SMOOTH_SECONDS_HAND_SETTING = "smooth_seconds_hand_setting"
const val USELESS_SETTING_USED_FOR_PREVIEW_SETTING = "useless_setting_used_for_preview"

/*
 * Creates user styles in the settings activity associated with the watch face, so users can
 * edit different parts of the watch face. In the renderer (after something has changed), the
 * watch face listens for a flow from the watch face API data layer and updates the watch face.
 */
fun createUserStyleSchema(context: Context): UserStyleSchema {
    val colorStyleSetting =
        UserStyleSetting.ListUserStyleSetting(
            UserStyleSetting.Id(COLOR_STYLE_SETTING),
            context.resources,
            R.string.colors_style_setting,
            R.string.colors_style_setting_description,
            null,
            ColorStyleIdAndResourceIds.toOptionList(context),
            WatchFaceLayer.ALL_WATCH_FACE_LAYERS
        )

    val drawComplicationsInAmbientSetting = UserStyleSetting.BooleanUserStyleSetting(
        UserStyleSetting.Id(DRAW_COMPLICATIONS_IN_AMBIENT_SETTING),
        context.resources,
        R.string.misc_complications_on_aod,
        R.string.misc_complications_on_aod_description,
        null,
        listOf(WatchFaceLayer.COMPLICATIONS),
        DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT
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

    val uselessSettingUsedForUpdatingPreview = UserStyleSetting.BooleanUserStyleSetting(
        UserStyleSetting.Id(USELESS_SETTING_USED_FOR_PREVIEW_SETTING),
        context.resources,
        R.string.misc_complications_on_aod,                             // TODO: change
        R.string.misc_complications_on_aod_description,        // TODO: change
        null,
        listOf(WatchFaceLayer.BASE),
        true            // TODO: change
    )

    return UserStyleSchema(
        listOf(
            colorStyleSetting,
            drawComplicationsInAmbientSetting,
            smoothSecondsHandSetting,
            uselessSettingUsedForUpdatingPreview
        )
    )
}