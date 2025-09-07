package nodomain.pacjo.wear.watchface.feature.hands.styles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
import nodomain.pacjo.wear.watchface.feature.hands.HandStyle
import nodomain.pacjo.wear.watchface.feature.hands.R
import androidx.core.graphics.toColorInt
import androidx.wear.watchface.DrawMode
import nodomain.pacjo.wear.watchface.feature.hands.utils.drawHands

object ModernHandStyle : HandStyle() {
    override val id: String = "modern"
    override val displayNameResourceId: Int = R.string.hands_modern_name

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            canvas.drawHands(
                bounds = bounds,
                zonedDateTime = zonedDateTime,
                basePaint = Paint().apply { isAntiAlias = true; color = "#D0D0D0".toColorInt() },
                drawHourHand = { baseHourPaint ->
                    drawCutoutHand(
                        canvas,
                        bounds,
                        bounds.width() * 0.08f,
                        bounds.height() * 0.4f,
                        baseHourPaint.apply { strokeWidth = 5f }
                    )
                },
                drawMinuteHand = { baseMinutePaint ->
                    drawCutoutHand(
                        canvas,
                        bounds,
                        bounds.width() * 0.05f,
                        bounds.height() * 0.5f,
                        baseMinutePaint.apply { strokeWidth = 4f }
                    )
                },
                drawSecondsHand = { baseSecondsPaint ->
                    if (renderingContext.renderParameters.drawMode != DrawMode.AMBIENT) {
                        drawSecondsHand(
                            canvas,
                            bounds,
                            bounds.width() * 0.03f,
                            bounds.height() * 0.55f,
                            baseSecondsPaint.apply { strokeWidth = 3f }
                        )
                    }
                }
            )

            // center point
            canvas.drawCircle(
                bounds.exactCenterX(),
                bounds.exactCenterY(),
                3f,
                Paint().apply { color = Color.BLACK }
            )
        }
        // TODO: support opengl
    }

    private fun drawCutoutHand(
        canvas: Canvas,
        bounds: Rect,
        handWidth: Float,
        handHeight: Float,
        paint: Paint
    ) {
        val radius = handWidth / 2f

        val leftSideX = bounds.exactCenterX() - radius
        val rightSideX = leftSideX + handWidth
        val topY = bounds.exactCenterY() - (handHeight - 2 * radius)  // rounded bottom has the height of double radius
        val roundedEndTopY = bounds.exactCenterY() - radius

        val outerPath = Path().apply {
            // start at left top corner
            moveTo(leftSideX, topY)
            // move to the start of the filled section on the left side
            lineTo(leftSideX, roundedEndTopY)
            // rounded end
            val arcOval = RectF(leftSideX, roundedEndTopY, rightSideX, roundedEndTopY + 2 * radius)
            arcTo(arcOval, 180f, -180f, false)
            // move to the right top corner
            lineTo(rightSideX, topY)
            // close, connecting the current and start positions automatically
            close()
        }

        val innerPath = Path().apply {
            val width = paint.strokeWidth
            addRect(
                leftSideX + width,
                topY + width,
                rightSideX - width,
                roundedEndTopY - 2 * width, // this works just as well when subtracting a single width, but doubling this makes it look a bit better
                Path.Direction.CW
            )
        }

        val path = Path().apply { op(outerPath, innerPath, Path.Op.DIFFERENCE) }
        canvas.drawPath(path, paint)
    }

    private fun drawSecondsHand(
        canvas: Canvas,
        bounds: Rect,
        handWidth: Float,
        handHeight: Float,
        paint: Paint
    ) {
        val radius = handWidth / 2f

        val leftSideX = bounds.exactCenterX() - radius
        val rightSideX = leftSideX + handWidth
        val baseHeight = handHeight / 6f
        val baseY = bounds.exactCenterY() + baseHeight

        val path = Path().apply {
            // start and bottom left corner
            moveTo(leftSideX, baseY)
            // while we could move to center top point
            // this would create a very fine point, which has troubles rendering
            // instead we'll create a very file trapezoid - almost a triangle, but not quite
//            lineTo(bounds.exactCenterX(), bounds.centerY() - handHeight + baseHeight)
            val topPointWidth = radius / 6f
            lineTo(bounds.exactCenterX() - topPointWidth, bounds.centerY() - handHeight + baseHeight)
            lineTo(bounds.exactCenterX() + topPointWidth, bounds.centerY() - handHeight + baseHeight)
            // move to bottom right corner
            lineTo(rightSideX, baseY)
            // close, connecting the current and start positions automatically
            close()

            addCircle(
                bounds.exactCenterX(),
                bounds.exactCenterY(),
                8f,
                Path.Direction.CW
            )
        }

        canvas.drawPath(path, paint)
    }
}