package nodomain.pacjo.wear.watchface.miss_minutes

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.toColorInt
import nodomain.pacjo.wear.watchface.shared.RenderingContext
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.shared.utils.drawTextInBounds
import nodomain.pacjo.wear.watchface.shared.GranularWatchFaceLayer

class WatchFaceRendererImpl(
    private val context: Context
) : WatchFaceRenderer {
    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            if (renderingContext.layer == GranularWatchFaceLayer.CLOCK) {
                val hour = zonedDateTime.hour.toString().padStart(2, '0')
                val minute = zonedDateTime.minute.toString().padStart(2, '0')
                val second = zonedDateTime.second.toString().padStart(2, '0')
                val paint = Paint().apply {
                    color = "#F5790C".toColorInt()
                    textSize = bounds.width() * 0.125f
                    typeface = context.resources.getFont(R.font.anonymous_pro)
                    isAntiAlias = true
                    setShadowLayer(5f, 5f, 5f, Color.BLACK)
                }

                canvas.drawTextInBounds(
                    "$hour:$minute:$second",
                    RectF(
                        bounds.width() * 0f,
                        bounds.height() * 0.6f,
                        bounds.width() * 1f,
                        bounds.height() * 0.8f
                    ),
                    paint
                )
            }
        }
    }
}