package nodomain.pacjo.wear.watchface.base

import android.graphics.Canvas
import android.graphics.Rect
import java.time.ZonedDateTime

/**
 * An interface defining the drawing contract for a watch face.
 */
interface WatchFaceRenderer {
    /**
     * Called on every draw cycle, should draw everything that isn't already handled in optional features.
     *
     * @param canvas [Canvas] to draw into
     * @param bounds rectangular bounds of the given canvas
     * @param zonedDateTime time which can be used to modify drawing based on current time
     */
    fun draw(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) { }
}