package nodomain.pacjo.wear.watchface.data.watchface

const val DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT = true

const val TIME_RING_CORNER_RADIUS_DEFAULT = 60f
const val TIME_RING_CORNER_RADIUS_MINIMUM = 10f
const val TIME_RING_CORNER_RADIUS_MAXIMUM = 100f
const val TIME_RING_CORNER_RADIUS_STEP = 5f

const val TIME_RING_WIDTH_DEFAULT = 20f
const val TIME_RING_WIDTH_MINIMUM = 10f
const val TIME_RING_WIDTH_MAXIMUM = 100f
const val TIME_RING_WIDTH_STEP = 5f

/**
 * Represents all data needed to render a watch face.
 */
data class WatchFaceData(
    val activeColorStyle: ColorStyleIdAndResourceIds = ColorStyleIdAndResourceIds.STYLE1,
    val ambientColorStyle: ColorStyleIdAndResourceIds = ColorStyleIdAndResourceIds.AMBIENT,
    val drawComplicationsInAmbient: Boolean = DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT,
    val timeRingWidth: Float = TIME_RING_WIDTH_DEFAULT,
    val timeRingCornerRadius: Float = TIME_RING_CORNER_RADIUS_DEFAULT
)