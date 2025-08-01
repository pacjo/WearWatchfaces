package nodomain.pacjo.wear.watchface.feature.base

import android.graphics.Canvas
import android.graphics.Rect
import androidx.wear.watchface.style.WatchFaceLayer
import java.time.ZonedDateTime

/**
 * An interface for any WatchFaceFeature that needs to draw on the canvas.
 * This allows the renderer to be completely decoupled from concrete feature types.
 */
interface DrawableFeature : WatchFaceFeature {
    /**
     * The layer on which this feature should be drawn.
     */
    val layer: WatchFaceLayer

    /**
     * The drawing command for this feature.
     * The renderer will call this method during its render pass.
     */
    fun draw(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime)
}