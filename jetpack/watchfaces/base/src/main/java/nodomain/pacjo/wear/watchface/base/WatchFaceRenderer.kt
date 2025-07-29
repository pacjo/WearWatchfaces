package nodomain.pacjo.wear.watchface.base

import android.graphics.Canvas
import android.graphics.Rect
import java.time.ZonedDateTime

/**
 * An interface defining the drawing contract for a watch face.
 */
// TODO: add docs
interface WatchFaceRenderer {
    fun drawBackground(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime)
    fun drawClock(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime)
    fun drawComplications(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime)
}