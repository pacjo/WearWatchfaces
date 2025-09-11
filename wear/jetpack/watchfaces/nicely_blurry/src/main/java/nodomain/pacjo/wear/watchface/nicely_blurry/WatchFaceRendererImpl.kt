package nodomain.pacjo.wear.watchface.nicely_blurry

import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.feature.digital_clock.utils.drawTextInBounds
import nodomain.pacjo.wear.watchface.feature.rendering.GranularWatchFaceLayer

class WatchFaceRendererImpl : WatchFaceRenderer {
    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            if (renderingContext.layer == GranularWatchFaceLayer.CLOCK) {
                val hour = zonedDateTime.hour.toString().padStart(2, '0')
                val minute = zonedDateTime.minute.toString().padStart(2, '0')
                val paint = Paint().apply {
                    color = Color.WHITE
                    textSize = bounds.width() * 0.25f
                    isAntiAlias = true
                    setShadowLayer(1f, 1f, 1f, Color.BLACK)
                }

                canvas.drawTextInBounds(
                    "$hour:$minute",
                    RectF(
                        bounds.width() * 0f,
                        bounds.height() * 0.17f,
                        bounds.width() * 1f,
                        bounds.height() * 0.27f
                    ),
                    paint
                )
            } else if (renderingContext.layer == GranularWatchFaceLayer.HEAVENS) {
                canvas.drawRoundRect(
                    bounds.width() * 0.18f,
                    bounds.height() * 0.70f,
                    bounds.width() * 0.82f,
                    bounds.height() * 0.90f,
                    50f,
                    50f,
                    Paint().apply { color = Color.WHITE }
                )
            }
        }
    }
}