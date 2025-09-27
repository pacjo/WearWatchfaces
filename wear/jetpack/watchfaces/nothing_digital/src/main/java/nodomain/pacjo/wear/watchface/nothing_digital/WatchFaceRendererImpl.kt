package nodomain.pacjo.wear.watchface.nothing_digital

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.feature.digital_clock.utils.drawTextInBounds
import nodomain.pacjo.wear.watchface.feature.rendering.GranularWatchFaceLayer

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
                    textSize = bounds.width() * 0.3f
                    typeface = context.resources.getFont(R.font.nothing_dot_57)
                    isAntiAlias = true
                }

                // TODO: convert to single call when multiline is supported
                canvas.drawTextInBounds(
                    hour,
                    RectF(
                        bounds.width() * 0f,
                        bounds.height() * 0.25f,
                        bounds.width() * 1f,
                        bounds.height() * 0.45f
                    ),
                    paint
                )
                canvas.drawTextInBounds(
                    minute,
                    RectF(
                        bounds.width() * 0f,
                        bounds.height() * 0.55f,
                        bounds.width() * 1f,
                        bounds.height() * 0.75f
                    ),
                    paint
                )
            }
        }
    }
}