package nodomain.pacjo.wear.watchface.feature.hands.styles

import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.withRotation
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
import nodomain.pacjo.wear.watchface.feature.hands.HandStyle
import nodomain.pacjo.wear.watchface.feature.hands.R
import nodomain.pacjo.wear.watchface.feature.hands.utils.getHandsStats

object ModernHandStyle : HandStyle() {
    override val id: String = "modern"
    override val displayNameResourceId: Int = R.string.hands_style1_name

    private val hourPaint = Paint().apply { color = Color.CYAN; isAntiAlias = true }
    private val minutePaint = Paint().apply { color = Color.CYAN; isAntiAlias = true }
    private val secondPaint = Paint().apply { color = Color.YELLOW; isAntiAlias = true }

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            val centerX = bounds.exactCenterX()
            val centerY = bounds.exactCenterY()

            val handsStats = getHandsStats(zonedDateTime)

            canvas.apply {
                withRotation(handsStats.hoursHandAngle, centerX, centerY) {
                    drawRect(
                        centerX - 6f,
                        centerY - (centerX * 0.5f),
                        centerX + 6f,
                        centerY + 12f,
                        hourPaint
                    )
                }

                withRotation(handsStats.minutesHandAngle, centerX, centerY) {
                    drawRect(
                        centerX - 4f,
                        centerY - (centerX * 0.75f),
                        centerX + 4f,
                        centerY + 16f,
                        minutePaint
                    )
                }

                withRotation(handsStats.secondsHandAngle, centerX, centerY) {
                    drawRect(
                        centerX - 2f,
                        centerY - (centerX * 0.9f),
                        centerX + 2f,
                        centerY,
                        secondPaint
                    )
                }
            }
        }
        // TODO: support opengl
    }
}