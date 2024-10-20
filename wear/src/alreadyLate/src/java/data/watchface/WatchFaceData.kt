package nodomain.pacjo.wear.watchface.data.watchface

const val DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT = true
const val SMOOTH_SECONDS_HAND_DEFAULT = true

/**
 * Represents all data needed to render a watch face.
 */
data class WatchFaceData(
    val activeColorStyle: ColorStyle = ColorStyle.STYLE1,
    val ambientColorStyle: ColorStyle = ColorStyle.AMBIENT,
    val handsStyle: HandsStyles = HandsStyles.MODERN,      // TODO: change default
    val backgroundStyle: BackgroundStyles = BackgroundStyles.STYLE1,
    val drawComplicationsInAmbient: Boolean = DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT,
    val smoothSecondsHand: Boolean = SMOOTH_SECONDS_HAND_DEFAULT
)