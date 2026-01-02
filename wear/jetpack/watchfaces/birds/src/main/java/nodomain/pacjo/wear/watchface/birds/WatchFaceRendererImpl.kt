package nodomain.pacjo.wear.watchface.birds

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
                    color = Color.BLACK
                    textSize = bounds.width() * 0.25f
                    typeface = context.resources.getFont(R.font.spline_sans_mono)
                    isAntiAlias = true
                }

                // TODO: convert to single call when multiline is supported
                canvas.drawTextInBounds(
                    hour,
                    RectF(
                        bounds.width() * 0.12f,
                        bounds.height() * 0.20f,
                        bounds.width() * 0.40f,
                        bounds.height() * 0.45f
                    ),
                    paint
                )
                canvas.drawTextInBounds(
                    minute,
                    RectF(
                        bounds.width() * 0.12f,
                        bounds.height() * 0.46f,
                        bounds.width() * 0.40f,
                        bounds.height() * 0.67f
                    ),
                    paint
                )
            }
        }
    }
}