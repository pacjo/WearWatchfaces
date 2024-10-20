package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSchema
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyle
import nodomain.pacjo.wear.watchface.data.watchface.DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT

const val COLOR_STYLE_SETTING = "color_style_setting"
const val DRAW_COMPLICATIONS_IN_AMBIENT_SETTING = "draw_complications_in_ambient_setting"

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

    val drawComplicationsInAmbientSetting = UserStyleSetting.BooleanUserStyleSetting(
        UserStyleSetting.Id(DRAW_COMPLICATIONS_IN_AMBIENT_SETTING),
        context.resources,
        R.string.misc_complications_on_aod,
        R.string.misc_complications_on_aod_description,
        null,
        listOf(WatchFaceLayer.COMPLICATIONS),
        DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT
    )

    return UserStyleSchema(
        listOf(
            colorStyleSetting,
            drawComplicationsInAmbientSetting
        )
    )
}