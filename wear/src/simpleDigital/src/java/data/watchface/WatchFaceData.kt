package nodomain.pacjo.wear.watchface.data.watchface

import nodomain.pacjo.wear.watchface.utils.DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT
import nodomain.pacjo.wear.watchface.utils.USE_CUSTOM_FONT_FOR_COMPLICATIONS_DEFAULT

const val TIME_RING_CORNER_RADIUS_DEFAULT = 60f
const val TIME_RING_CORNER_RADIUS_MINIMUM = 10f
const val TIME_RING_CORNER_RADIUS_MAXIMUM = 100f
const val TIME_RING_CORNER_RADIUS_STEP = 5f

const val TIME_RING_WIDTH_DEFAULT = 30f
const val TIME_RING_WIDTH_MINIMUM = 10f
const val TIME_RING_WIDTH_MAXIMUM = 100f
const val TIME_RING_WIDTH_STEP = 5f

/**
 * Represents all data needed to render a watch face.
 */
data class WatchFaceData(
    val activeColorStyle: ColorStyle = ColorStyle.STYLE1,
    val ambientColorStyle: ColorStyle = ColorStyle.AMBIENT,
    val font: Fonts = Fonts.DEFAULT,
    val useCustomFontForComplications: Boolean = USE_CUSTOM_FONT_FOR_COMPLICATIONS_DEFAULT,
    val drawComplicationsInAmbient: Boolean = DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT,
    val timeRingWidth: Float = TIME_RING_WIDTH_DEFAULT,
    val timeRingCornerRadius: Float = TIME_RING_CORNER_RADIUS_DEFAULT
)