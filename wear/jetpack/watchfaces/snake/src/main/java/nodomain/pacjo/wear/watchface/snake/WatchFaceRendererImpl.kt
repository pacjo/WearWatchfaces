package nodomain.pacjo.wear.watchface.snake

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.feature.digital_clock.utils.Alignment
import nodomain.pacjo.wear.watchface.feature.digital_clock.utils.drawTextInBounds

class WatchFaceRendererImpl(
    private val context: Context
) : WatchFaceRenderer {
    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            val hour = zonedDateTime.hour.toString().padStart(2, '0')
            val minute = zonedDateTime.minute.toString().padStart(2, '0')
            val paint = Paint().apply {
                color = Color.WHITE
                textSize = bounds.width() * 0.3f
                typeface = context.resources.getFont(R.font.ibm_mda)
                isAntiAlias = true
                setShadowLayer(5f, 5f, 5f, Color.BLACK)
            }

            // TODO: convert to single call when multiline is supported
            canvas.drawTextInBounds(
                hour,
                RectF(
                    bounds.width() * 0f,
                    bounds.height() * 0.3f,
                    bounds.width() * 1f,
                    bounds.height() * 0.45f
                ),
                paint,
                Alignment.CENTER
            )
            canvas.drawTextInBounds(
                minute,
                RectF(
                    bounds.width() * 0f,
                    bounds.height() * 0.55f,
                    bounds.width() * 1f,
                    bounds.height() * 0.7f
                ),
                paint,
                Alignment.CENTER
            )
        }
    }
}