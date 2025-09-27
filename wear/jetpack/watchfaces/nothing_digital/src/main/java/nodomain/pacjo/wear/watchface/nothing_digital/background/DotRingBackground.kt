package nodomain.pacjo.wear.watchface.nothing_digital.background

import android.graphics.Color
import android.graphics.Paint
import nodomain.pacjo.wear.watchface.feature.background.Background
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import nodomain.pacjo.wear.watchface.nothing_digital.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object DotRingBackground : Background() {
    override val id = "tva_grid"
    override val displayNameResourceId = R.string.dot_ring_background


    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            // fill background
            canvas.drawColor(Color.BLACK)

            // draw dots - TODO: draw rounded rectangle on rectangular screens
            val dotRingRadius = (min(bounds.width(), bounds.height()) / 2f) * 0.95f
            val secondFraction = zonedDateTime.nano / 1_000_000_000f

            val numberOfDots = 60

            val activeDotSize = 4f
            val inactiveDotSize = 3f

            val dotPaint = Paint().apply { isAntiAlias = true; color = Color.WHITE }

            (0..<60).forEach { index ->
                // gradually change dot size and opacity
                val dotSize: Float
                val dotAlpha: Int
                when {
                    index < zonedDateTime.second -> {
                        dotSize = activeDotSize
                        dotAlpha = 255
                    }
                    index == zonedDateTime.second -> {
                        dotSize = max(inactiveDotSize, activeDotSize * secondFraction)
                        dotAlpha = max(100, (255 * secondFraction).toInt())
                    }
                    else -> {
                        dotSize = inactiveDotSize
                        dotAlpha = 100
                    }
                }

                dotPaint.apply { alpha = dotAlpha }

                val angle = (index.toFloat() / numberOfDots) * 2 * PI
                val dotCenterX = bounds.exactCenterX() + sin(angle).toFloat() * dotRingRadius
                val dotCenterY = bounds.exactCenterY() - cos(angle).toFloat() * dotRingRadius

                canvas.drawCircle(dotCenterX, dotCenterY, dotSize, dotPaint)
            }
        }
        // TODO: support opengl
    }
}