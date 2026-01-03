package nodomain.pacjo.wear.watchface.pointy_damascus

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.shared.utils.drawTextInBounds
import nodomain.pacjo.wear.watchface.shared.GranularWatchFaceLayer
import nodomain.pacjo.wear.watchface.shared.RenderingContext

class WatchFaceRendererImpl(
    private val context: Context
) : WatchFaceRenderer {
    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            if (renderingContext.layer == GranularWatchFaceLayer.CLOCK) {
                val hour = zonedDateTime.hour.toString().padStart(2, '0')
                val minute = zonedDateTime.minute.toString().padStart(2, '0')
                val paint = Paint().apply {
                    color = Color.WHITE
                    textSize = bounds.width() * 0.175f
                    typeface = context.resources.getFont(R.font.ocr_a)
                }

                val textBounds = RectF(
                    bounds.width() * 0.22f,
                    bounds.height() * 0.72f,
                    bounds.width() * 0.78f,
                    bounds.height() * 0.9f
                )
                val cornerRadius = 15f

                canvas.drawRoundRect(
                    textBounds,
                    cornerRadius,
                    cornerRadius,
                    Paint().apply { color = Color.BLACK; alpha = 220 }
                )
                canvas.drawTextInBounds(
                    "$hour:$minute",
                    textBounds,
                    paint
                )
            }
        }
    }
}