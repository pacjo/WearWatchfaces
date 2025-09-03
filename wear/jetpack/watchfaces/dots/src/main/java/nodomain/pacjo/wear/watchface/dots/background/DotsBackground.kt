package nodomain.pacjo.wear.watchface.dots.background

import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.withRotation
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
import nodomain.pacjo.wear.watchface.dots.R
import nodomain.pacjo.wear.watchface.feature.background.Background
import kotlin.math.min

object DotsBackground : Background() {
    override val id = "dots"
    override val displayNameResourceId = R.string.dots_background

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            canvas.drawColor(Color.WHITE)

            val circlePaint = Paint().apply {
                color = Color.BLACK
                isAntiAlias = true
            }
            val circleRadius = min(bounds.width(), bounds.height()) * 0.05f
            val circleSpacing = circleRadius * 3.2f // center-to-center distance

            val extraCircles = 4
            val extraCirclesOffset = extraCircles / 2
            val numCirclesX = (bounds.width() / circleSpacing).toInt() + extraCircles
            val numCirclesY = (bounds.height() / circleSpacing).toInt() + extraCircles

            canvas.withRotation(30f) {
                for (numCircleY in 0..numCirclesY) {
                    for (numCircleX in 0..numCirclesX) {
                        drawCircle(
                            (numCircleX - extraCirclesOffset) * circleSpacing,
                            (numCircleY - extraCirclesOffset) * circleSpacing,
                            circleRadius,
                            circlePaint
                        )
                    }
                }
            }
        }
        // TODO: support opengl
    }
}