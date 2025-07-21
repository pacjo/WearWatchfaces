package nodomain.pacjo.wear.watchface.hands

import android.graphics.Canvas
import android.graphics.Rect
import nodomain.pacjo.wear.watchface.feature.base.FeatureOption
import java.time.ZonedDateTime

/**
 * The contract for a specific clock hand style.
 * It is a FeatureOption, so it has an ID and name, and it can draw a preview.
 * It also knows how to draw the actual hands on the watch face.
 */
interface HandStyle : FeatureOption {
    fun draw(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime)
}