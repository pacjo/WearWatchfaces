package nodomain.pacjo.wear.watchface.snake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer

class WatchFaceRendererImpl(
    private val context: Context
) : WatchFaceRenderer {
    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvasBackend ->
            val hour = canvasBackend.zonedDateTime.hour.toString().padStart(2, '0')
            val minute = canvasBackend.zonedDateTime.minute.toString().padStart(2, '0')
            val paint = Paint().apply {
                color = Color.WHITE
                textSize = canvasBackend.bounds.width() * 0.3f
                typeface = context.resources.getFont(R.font.ibm_mda)
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

            drawTextCentredBoth(canvasBackend.canvas, paint, hour, canvasBackend.bounds.exactCenterX(), canvasBackend.bounds.exactCenterY() * 0.75f)
            drawTextCentredBoth(canvasBackend.canvas, paint, minute, canvasBackend.bounds.exactCenterX(), canvasBackend.bounds.exactCenterY() * 1.25f)
        }
    }
}