package nodomain.pacjo.wear.watchface.data.watchface

const val DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT = true

/**
 * Represents all data needed to render a watch face.
 */
data class WatchFaceData(
    val activeColorStyle: ColorStyle = ColorStyle.STYLE1,
    val ambientColorStyle: ColorStyle = ColorStyle.AMBIENT,
    val drawComplicationsInAmbient: Boolean = DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT
)