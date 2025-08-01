package nodomain.pacjo.wear.watchface.feature.background

import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import nodomain.pacjo.wear.watchface.feature.base.FeatureOption

/**
 * The contract for a specific background.
 * It is a FeatureOption, so it has an ID and name, and it can draw a preview.
 * It also knows how to draw the actual background on the watch face.
 */
abstract class Background : FeatureOption {
    abstract fun draw(canvas: Canvas, bounds: Rect)

    final override fun drawPreview(canvas: Canvas, bounds: Rect) {
        Log.d(TAG, "preview called")

        draw(canvas, bounds)
    }

    companion object {
        const val TAG = "Background"
    }
}