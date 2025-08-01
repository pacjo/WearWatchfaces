package nodomain.pacjo.wear.watchface.jimball

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import nodomain.pacjo.wear.watchface.base.WatchFaceRenderer
import java.time.ZonedDateTime
import androidx.core.graphics.toColorInt

class WatchFaceRendererImpl(
    private val context: Context
) : WatchFaceRenderer {
    override fun drawBackground(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        // TODO: draw gif
    }

    override fun drawClock(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        val hour = zonedDateTime.hour.toString().padStart(2, '0')
        val minute = zonedDateTime.minute.toString().padStart(2, '0')
        val paint = Paint().apply {
            color = "#FF9800".toColorInt()
            textSize = bounds.width() * 0.225f
            typeface = context.resources.getFont(R.font.balatro)
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

    override fun drawComplications(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        // TODO: implement
    }
}