package nodomain.pacjo.wear.watchface.feature.hands.styles

import android.graphics.Color
import android.graphics.Paint
import androidx.wear.watchface.DrawMode
import nodomain.pacjo.wear.watchface.shared.RenderingContext
import nodomain.pacjo.wear.watchface.feature.hands.HandStyle
import nodomain.pacjo.wear.watchface.feature.hands.R
import nodomain.pacjo.wear.watchface.feature.hands.utils.drawHands

class BoldHandStyle(
    val fillHourHand: Boolean
) : HandStyle() {
    override val id: String = "bold_$fillHourHand"
    override val displayNameResourceId: Int =
        if (fillHourHand)
            R.string.hands_bold_name
        else
            R.string.hands_bold_outline_name

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            canvas.drawHands(
                bounds = bounds,
                zonedDateTime = zonedDateTime,
                basePaint = Paint().apply { isAntiAlias = true; strokeCap = Paint.Cap.ROUND },
                drawHourHand = { baseHourPaint ->
                    val hourHandWidth = 30f
                    val hourHandRadius = hourHandWidth / 2f
                    canvas.drawRoundRect(
                        bounds.exactCenterX() - hourHandRadius,
                        bounds.exactCenterY() * 0.35f,
                        bounds.exactCenterX() + hourHandRadius,
                        bounds.exactCenterX() + hourHandRadius,
                        hourHandWidth,
                        hourHandWidth,
                        baseHourPaint.apply {
                            color = Color.LTGRAY
                            strokeWidth = 6f
                            style =
                                if (fillHourHand)
                                    Paint.Style.FILL_AND_STROKE
                                else
                                    Paint.Style.STROKE
                        }
                    )
                },
                drawMinuteHand = { baseMinutePaint ->
                    canvas.drawLine(
                        bounds.exactCenterX(),
                        bounds.exactCenterY(),
                        bounds.exactCenterX(),
                        bounds.exactCenterY() * 0.25f,
                        baseMinutePaint.apply { color = Color.LTGRAY; strokeWidth = 9f }
                    )
                },
                drawSecondsHand = { baseSecondsPaint ->
                    if (renderingContext.renderParameters.drawMode != DrawMode.AMBIENT) {
                        canvas.drawLine(
                            bounds.exactCenterX(),
                            bounds.exactCenterY(),
                            bounds.exactCenterX(),
                            bounds.exactCenterY() * 0.1f,
                            baseSecondsPaint.apply { color = Color.DKGRAY; strokeWidth = 5f }
                        )
                    }
                }
            )

            // center point
            canvas.drawCircle(
                bounds.exactCenterX(),
                bounds.exactCenterY(),
                9f / 2f,        // half of minute hand paint
                Paint().apply { isAntiAlias = true; color = Color.DKGRAY }
            )
        }
        // TODO: support opengl
    }
}