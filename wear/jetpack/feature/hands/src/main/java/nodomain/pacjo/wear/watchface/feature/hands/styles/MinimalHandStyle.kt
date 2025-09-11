package nodomain.pacjo.wear.watchface.feature.hands.styles

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import nodomain.pacjo.wear.watchface.feature.hands.R
import nodomain.pacjo.wear.watchface.feature.hands.HandStyle
import nodomain.pacjo.wear.watchface.feature.hands.utils.drawHands

object MinimalHandStyle : HandStyle() {
    override val id: String = "minimal"
    override val displayNameResourceId: Int = R.string.hands_minimal_name

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            canvas.drawHands(
                bounds = bounds,
                zonedDateTime = zonedDateTime,
                basePaint = Paint().apply { isAntiAlias = true; strokeCap = Paint.Cap.ROUND },
                drawHourHand = { baseHourPaint ->
                    canvas.drawLine(
                        bounds.exactCenterX(),
                        bounds.exactCenterY(),
                        bounds.exactCenterX(),
                        bounds.exactCenterY() * 0.45f,
                        baseHourPaint.apply { color = Color.BLACK; strokeWidth = 20f }       // TODO: use color primary
                    )
                },
                drawMinuteHand = { baseMinutePaint ->
                    canvas.drawLine(
                        bounds.exactCenterX(),
                        bounds.exactCenterY(),
                        bounds.exactCenterX(),
                        bounds.exactCenterY() * 0.25f,
                        baseMinutePaint.apply { color = Color.BLACK; strokeWidth = 16f }     // TODO: use color secondary
                    )
                },
                drawSecondsHand = { /* no-op */ }
            )

            // (hollow) center point
            val outerRadius = 4f
            val outerPath = Path().apply {
                addCircle(
                    bounds.exactCenterX(),
                    bounds.exactCenterY(),
                    outerRadius,
                    Path.Direction.CW
                )
            }
            val innerPath = Path().apply {
                addCircle(
                    bounds.exactCenterX(),
                    bounds.exactCenterY(),
                    outerRadius / 2f,
                    Path.Direction.CW
                )
            }
            canvas.drawPath(
                Path().apply { op(outerPath, innerPath, Path.Op.DIFFERENCE) },
                Paint().apply { isAntiAlias = true; color = Color.DKGRAY }
            )
        }
        // TODO: support opengl
    }
}