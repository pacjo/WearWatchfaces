package nodomain.pacjo.wear.watchface.base.utils

import android.graphics.RectF

fun centeredRectF(
    centerX: Float,
    centerY: Float,
    width: Float,
    height: Float
): RectF {
    return RectF(
        centerX - width / 2f,
        centerY - height / 2f,
        centerX + width / 2f,
        centerY + height / 2f
    )
}

fun centeredRectF(
    centerX: Float,
    centerY: Float,
    size: Float
) = centeredRectF(centerX, centerY, size, size)