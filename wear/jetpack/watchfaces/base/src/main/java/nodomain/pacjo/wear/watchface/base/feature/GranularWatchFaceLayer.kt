package nodomain.pacjo.wear.watchface.base.feature

import androidx.wear.watchface.style.WatchFaceLayer

enum class GranularWatchFaceLayer {

    /**
     * Background images, background components the lowest layer.
     */
    BACKGROUND,

    /**
     * The main place where everything interesting happens.
     */
    MIDDLE_EARTH,

    /**
     * Time elements like digital clock of clock hands.
     */
    CLOCK,

    /**
     * Clock overlay.
     */
    HEAVENS,

    /**
     * Complications.
     */
    COMPLICATIONS,

    /**
     * Top overlay. Rendered over everything, including complications.
     */
    OVERLAY;

    fun toWatchFaceLayer(): WatchFaceLayer {
        return when (this) {
            OVERLAY -> WatchFaceLayer.COMPLICATIONS_OVERLAY
            COMPLICATIONS -> WatchFaceLayer.COMPLICATIONS

            else -> WatchFaceLayer.BASE
        }
    }
}