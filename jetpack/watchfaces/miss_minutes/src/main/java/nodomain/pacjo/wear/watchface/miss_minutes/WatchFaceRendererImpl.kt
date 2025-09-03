package nodomain.pacjo.wear.watchface.miss_minutes

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.toColorInt
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer

class WatchFaceRendererImpl(
    private val context: Context
) : WatchFaceRenderer {
    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvasBackend ->
            val hour = canvasBackend.zonedDateTime.hour.toString().padStart(2, '0')
            val minute = canvasBackend.zonedDateTime.minute.toString().padStart(2, '0')
            val second = canvasBackend.zonedDateTime.second.toString().padStart(2, '0')
            val paint = Paint().apply {
                color = "#F5790C".toColorInt()
                textSize = canvasBackend.bounds.width() * 0.125f
                typeface = context.resources.getFont(R.font.anonymous_pro)
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

            drawTextCentredBoth(canvasBackend.canvas, paint, "$hour:$minute:$second", canvasBackend.bounds.exactCenterX(), canvasBackend.bounds.height() * 0.70f)
        }
    }
}