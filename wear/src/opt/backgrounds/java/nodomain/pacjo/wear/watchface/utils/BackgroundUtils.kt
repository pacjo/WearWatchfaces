package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import nodomain.pacjo.wear.watchface.data.watchface.WatchFaceData

fun drawBackground(
    context: Context,
    watchFaceData: WatchFaceData,
    canvas: Canvas,
    bounds: Rect
) {
    val backgroundDrawable = watchFaceData.backgroundStyle.backgroundDrawableResourceId

    val vectorBackground = VectorDrawableCompat.create(context.resources, backgroundDrawable, null)
    vectorBackground?.bounds = bounds
    vectorBackground?.draw(canvas)
}