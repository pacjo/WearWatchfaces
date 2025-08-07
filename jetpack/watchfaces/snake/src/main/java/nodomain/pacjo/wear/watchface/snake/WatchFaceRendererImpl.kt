package nodomain.pacjo.wear.watchface.snake

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import nodomain.pacjo.wear.watchface.base.WatchFaceRenderer
import java.time.ZonedDateTime

class WatchFaceRendererImpl : WatchFaceRenderer {
    override fun draw(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        val hour = zonedDateTime.hour.toString().padStart(2, '0')
        val minute = zonedDateTime.minute.toString().padStart(2, '0')
        val paint = Paint().apply {
            color = Color.WHITE
            textSize = bounds.width() * 0.225f
            isAntiAlias = true
            setShadowLayer(5f, 5f, 5f, Color.BLACK)
        }

        // TODO: rewrite and move
        fun drawTextCentredBoth(canvas: Canvas, paint: Paint, text: String, cx: Float, cy: Float) {
            val textBounds = Rect()

            paint.getTextBounds(text, 0, text.length, textBounds)

            canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint.apply {
                textAlign = Paint.Align.CENTER
            })
        }

        drawTextCentredBoth(canvas, paint, hour, bounds.exactCenterX(), bounds.exactCenterY() * 0.75f)
        drawTextCentredBoth(canvas, paint, minute, bounds.exactCenterX(), bounds.exactCenterY() * 1.25f)
    }
}