package data.watchface

import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds

const val DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT = true

/**
 * Represents all data needed to render a watch face.
 */
data class WatchFaceData(
    val activeColorStyle: ColorStyleIdAndResourceIds = ColorStyleIdAndResourceIds.STYLE1,
    val ambientColorStyle: ColorStyleIdAndResourceIds = ColorStyleIdAndResourceIds.AMBIENT,
    val drawComplicationsInAmbient: Boolean = DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT
)