package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import androidx.core.graphics.ColorUtils
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import nodomain.pacjo.wear.watchface.data.watchface.WatchFaceData

fun drawBackground(
    context: Context,
    watchFaceData: WatchFaceData,
    renderParameters: RenderParameters,
    canvas: Canvas,
    bounds: Rect
) {
    val backgroundDrawable = watchFaceData.backgroundStyle.backgroundDrawableRes

    val vectorBackground = VectorDrawableCompat.create(context.resources, backgroundDrawable, null)
    vectorBackground?.bounds = bounds
    vectorBackground?.draw(canvas)

    val ambientTint = ColorUtils.setAlphaComponent(Color.BLACK, 200)
    if (renderParameters.drawMode == DrawMode.AMBIENT)
        canvas.drawColor(ambientTint)
}