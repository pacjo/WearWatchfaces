package nodomain.pacjo.wear.watchface.new_year

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.graphics.toColorInt
import nodomain.pacjo.wear.watchface.shared.RenderingContext
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.shared.utils.drawTextInBounds
import nodomain.pacjo.wear.watchface.shared.GranularWatchFaceLayer
import java.time.format.DateTimeFormatter

class WatchFaceRendererImpl(
    private val context: Context
) : WatchFaceRenderer {
    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            when (renderingContext.layer) {
                GranularWatchFaceLayer.BACKGROUND -> {
                    canvas.drawColor(Color.BLACK)
                }
                GranularWatchFaceLayer.CLOCK -> {
                    val hour = zonedDateTime.hour.toString().padStart(2, '0')
                    val minute = zonedDateTime.minute.toString().padStart(2, '0')
                    val yearPaint = Paint().apply {
                        isAntiAlias = true
                        color = "#FFA200".toColorInt()
                        textSize = bounds.width() * 0.25f
                        typeface = context.resources.getFont(R.font.dancing_script)
                        fontVariationSettings = "'wght' 700"
                        setShadowLayer(4f, 2f, 2f, Color.RED)
                    }
                    val dateTimePaint = Paint().apply {
                        isAntiAlias = true
                        color = Color.LTGRAY
                        typeface = Typeface.DEFAULT
                    }

                    // year
                    canvas.drawTextInBounds(
                        zonedDateTime.year.toString(),
                        RectF(
                            bounds.width() * 0f,
                            bounds.height() * 0.25f,
                            bounds.width() * 1f,
                            bounds.height() * 0.55f
                        ),
                        yearPaint
                    )

                    // time
                    canvas.drawTextInBounds(
                        "$hour:$minute",
                        RectF(
                            bounds.width() * 0f,
                            bounds.height() * 0.72f,
                            bounds.width() * 1f,
                            bounds.height() * 0.82f
                        ),
                        dateTimePaint.apply { textSize = bounds.width() * 0.1f }
                    )
                    // date
                    canvas.drawTextInBounds(
                        DateTimeFormatter.ofPattern("EE, MMM ee").format(zonedDateTime),
                        RectF(
                            bounds.width() * 0f,
                            bounds.height() * 0.80f,
                            bounds.width() * 1f,
                            bounds.height() * 0.90f
                        ),
                        dateTimePaint.apply { textSize = bounds.width() * 0.04f }
                    )
                }

                else -> { /* no-op */ }
            }
        }
   }
}