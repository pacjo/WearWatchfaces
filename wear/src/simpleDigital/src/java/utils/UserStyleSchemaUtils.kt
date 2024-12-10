package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import androidx.wear.watchface.style.UserStyleSchema
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_CORNER_RADIUS_DEFAULT
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_CORNER_RADIUS_MAXIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_CORNER_RADIUS_MINIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_WIDTH_DEFAULT
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_WIDTH_MAXIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_WIDTH_MINIMUM

const val TIME_RING_CORNER_RADIUS_SETTING = "time_ring_corner_radius_setting"
const val TIME_RING_WIDTH_SETTING = "time_ring_width_setting"

fun createUserStyleSchema(context: Context): UserStyleSchema {
    val timeRingCornerRadiusSetting = UserStyleSetting.DoubleRangeUserStyleSetting(
        UserStyleSetting.Id(TIME_RING_CORNER_RADIUS_SETTING),
        context.resources,
        R.string.time_ring_radius,
        R.string.time_ring_radius_description,
        null,
        TIME_RING_CORNER_RADIUS_MINIMUM.toDouble(),
        TIME_RING_CORNER_RADIUS_MAXIMUM.toDouble(),
        listOf(WatchFaceLayer.BASE),
        TIME_RING_CORNER_RADIUS_DEFAULT.toDouble()
    )

    val timeRingWidthSetting = UserStyleSetting.DoubleRangeUserStyleSetting(
        UserStyleSetting.Id(TIME_RING_WIDTH_SETTING),
        context.resources,
        R.string.time_ring_width,
        R.string.time_ring_width_description,
        null,
        TIME_RING_WIDTH_MINIMUM.toDouble(),
        TIME_RING_WIDTH_MAXIMUM.toDouble(),
        listOf(WatchFaceLayer.BASE),
        TIME_RING_WIDTH_DEFAULT.toDouble()
    )

    return UserStyleSchema(
        createBaseUserStyleSettings(context) +
        createFontUserStyleSettings(context) +
        createComplicationsUserStyleSettings(context) +
        listOf(
            timeRingWidthSetting,
            timeRingCornerRadiusSetting
        )
    )
}
