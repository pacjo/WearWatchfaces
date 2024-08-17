package nodomain.pacjo.wear.watchface.data.watchface

const val SMOOTH_SECONDS_HAND_DEFAULT = true

/**
 * Represents all data needed to render a watch face.
 */
data class WatchFaceData(
    val activeColorStyle: ColorStyleIdAndResourceIds = ColorStyleIdAndResourceIds.STYLE1,
    val ambientColorStyle: ColorStyleIdAndResourceIds = ColorStyleIdAndResourceIds.AMBIENT,
    val backgroundStyle: BackgroundStyles = BackgroundStyles.STYLE1,
    val handsStyle: HandsStyles = HandsStyles.MODERN,      // TODO: change default
    val smoothSecondsHand: Boolean = SMOOTH_SECONDS_HAND_DEFAULT
)