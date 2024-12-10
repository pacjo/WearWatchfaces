package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.R

const val DRAW_COMPLICATIONS_IN_AMBIENT_SETTING = "draw_complications_in_ambient_setting"
const val DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT = true

/**
 * Creates [UserStyleSetting]s for hands configuration.
 */
fun createComplicationsUserStyleSettings(context: Context): List<UserStyleSetting> {
    val drawComplicationsInAmbientSetting = UserStyleSetting.BooleanUserStyleSetting(
        UserStyleSetting.Id(DRAW_COMPLICATIONS_IN_AMBIENT_SETTING),
        context.resources,
        R.string.misc_complications_on_aod,
        R.string.misc_complications_on_aod_description,
        null,
        listOf(WatchFaceLayer.COMPLICATIONS),
        DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT
    )

    return listOf(drawComplicationsInAmbientSetting)
}