package nodomain.pacjo.wear.watchface.feature.hands.styles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
import nodomain.pacjo.wear.watchface.feature.hands.R
import nodomain.pacjo.wear.watchface.feature.hands.HandStyle
import nodomain.pacjo.wear.watchface.feature.hands.utils.getHandsStats
import kotlin.math.cos
import kotlin.math.sin

object ClassicHandStyle : HandStyle() {
    override val id: String = "classic"
    override val displayNameResourceId: Int = R.string.hands_style2_name

    private val hourPaint = Paint().apply { color = Color.DKGRAY; strokeWidth = 8f; isAntiAlias = true; strokeCap = Paint.Cap.ROUND }
    private val minutePaint = Paint().apply { color = Color.GRAY; strokeWidth = 5f; isAntiAlias = true; strokeCap = Paint.Cap.ROUND }
    private val secondPaint = Paint().apply { color = Color.LTGRAY; strokeWidth = 3f; isAntiAlias = true; strokeCap = Paint.Cap.ROUND }

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            val handsStats = getHandsStats(zonedDateTime)

            fun drawHand(canvas: Canvas, bounds: Rect, angleDegrees: Float, lengthFraction: Float, paint: Paint) {
                val centerX = bounds.exactCenterX()
                val centerY = bounds.exactCenterY()
                val length = centerX * lengthFraction
                val angleRadians = Math.toRadians(angleDegrees.toDouble() - 180)
                val endX = centerX + (sin(angleRadians) * length).toFloat()
                val endY = centerY + (cos(angleRadians) * length).toFloat()
                canvas.drawLine(centerX, centerY, endX, endY, paint)
            }

            drawHand(canvas, bounds, -handsStats.hoursHandAngle, 0.5f, hourPaint)
            drawHand(canvas, bounds, -handsStats.minutesHandAngle, 0.75f, minutePaint)
            drawHand(canvas, bounds, -handsStats.secondsHandAngle, 0.9f, secondPaint)
        }
        // TODO: support opengl
    }
}