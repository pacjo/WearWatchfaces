package nodomain.pacjo.wear.watchface.feature.hands

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import nodomain.pacjo.wear.watchface.feature.base.FeatureOption
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/**
 * The contract for a specific clock hand style.
 * It is a FeatureOption, so it has an ID and name, and it can draw a preview.
 * It also knows how to draw the actual hands on the watch face.
 */
abstract class HandStyle : FeatureOption {
    abstract fun draw(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime)

    final override fun drawPreview(canvas: Canvas, bounds: Rect) {
        Log.d(TAG, "preview called")

        // preview time to set the hour, minute and seconds hands in predetermined positions
        val zonedDateTime = ZonedDateTime.now()
            .withHour(10)
            .withMinute(10)
            .withSecond(30)
            .truncatedTo(ChronoUnit.SECONDS)

        draw(canvas, bounds, zonedDateTime)
    }

    companion object {
        const val TAG = "HandStyle"
    }
}