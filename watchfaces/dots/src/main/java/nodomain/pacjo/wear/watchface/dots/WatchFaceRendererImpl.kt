package nodomain.pacjo.wear.watchface.dots

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.withRotation
import nodomain.pacjo.wear.watchface.base.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.feature.hands.HandStyleFeature
import java.time.ZonedDateTime
import kotlin.math.min

class WatchFaceRendererImpl : WatchFaceRenderer {
    override fun drawBackground(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        canvas.drawColor(Color.WHITE)

        val circlePaint = Paint().apply {
            color = Color.BLACK
        }
        val circleRadius = min(bounds.width(), bounds.height()) * 0.05f
        val circleSpacing = circleRadius * 3.2f // center-to-center distance

        val extraCircles = 4
        val extraCirclesOffset = extraCircles / 2
        val numCirclesX = (bounds.width() / circleSpacing).toInt() + extraCircles
        val numCirclesY = (bounds.height() / circleSpacing).toInt() + extraCircles

        canvas.withRotation(30f) {
            for (numCircleY in 0..numCirclesY) {
                for (numCircleX in 0..numCirclesX) {
                    drawCircle(
                        (numCircleX - extraCirclesOffset) * circleSpacing,
                        (numCircleY - extraCirclesOffset) * circleSpacing,
                        circleRadius,
                        circlePaint
                    )
                }
            }
        }
    }

    override fun drawClock(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        // TODO: implement
    }

    override fun drawComplications(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        // TODO: implement
    }
}