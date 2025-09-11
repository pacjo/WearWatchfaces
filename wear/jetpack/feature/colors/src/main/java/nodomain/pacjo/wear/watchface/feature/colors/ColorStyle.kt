package nodomain.pacjo.wear.watchface.feature.colors

import android.graphics.Canvas
import android.graphics.Paint
import android.util.Log
import androidx.annotation.ColorInt
import androidx.core.graphics.toRectF
import nodomain.pacjo.wear.watchface.feature.base.FeatureOption
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext

/**
 * The specific color style.
 */
class ColorStyle(
    @ColorInt val primary: Int,
    @ColorInt val secondary: Int,
    @ColorInt val tertiary: Int,
    @ColorInt val background: Int,
) : FeatureOption {

    override val id = "color_style_${primary}_${secondary}_${tertiary}_${background}"
    override val displayNameResourceId = R.string.color_style_setting       // TODO: change

    override fun drawPreview(renderingContext: RenderingContext) {
        Log.d(TAG, "preview called")

        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            canvas.drawColor(background)

            fun Canvas.drawCirclePart(startAngle: Float, sweepAngle: Float, @ColorInt color: Int) {
                drawArc(
                    bounds.toRectF(),
                    startAngle,
                    sweepAngle,
                    true,
                    Paint().apply { this.color = color }
                )
            }

            // top half circle
            canvas.drawCirclePart(-180f, 180f, primary)
            // right quarter
            canvas.drawCirclePart(0f, 90f, tertiary)
            // left quarter
            canvas.drawCirclePart(90f, 90f, secondary)
        }
    }

    companion object {
        const val TAG = "ColorStyle"
    }
}