package nodomain.pacjo.wear.watchface.feature.hands.styles

import android.graphics.Color
import android.graphics.Paint
import androidx.wear.watchface.DrawMode
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import nodomain.pacjo.wear.watchface.feature.hands.R
import nodomain.pacjo.wear.watchface.feature.hands.HandStyle
import nodomain.pacjo.wear.watchface.feature.hands.utils.drawHands

object SimpleHandStyle : HandStyle() {
    override val id: String = "simple"
    override val displayNameResourceId: Int = R.string.hands_simple_name

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
                        bounds.exactCenterY() * 0.5f,
                        baseHourPaint.apply { color = Color.DKGRAY; strokeWidth = 8f }
                    )
                },
                drawMinuteHand = { baseMinutePaint ->
                    canvas.drawLine(
                        bounds.exactCenterX(),
                        bounds.exactCenterY(),
                        bounds.exactCenterX(),
                        bounds.exactCenterY() * 0.25f,
                        baseMinutePaint.apply { color = Color.GRAY; strokeWidth = 5f }
                    )
                },
                drawSecondsHand = { baseSecondsPaint ->
                    if (renderingContext.renderParameters.drawMode != DrawMode.AMBIENT) {
                        canvas.drawLine(
                            bounds.exactCenterX(),
                            bounds.exactCenterY(),
                            bounds.exactCenterX(),
                            bounds.exactCenterY() * 0.1f,
                            baseSecondsPaint.apply { color = Color.LTGRAY; strokeWidth = 3f }
                        )
                    }
                }
            )
        }
        // TODO: support opengl
    }
}