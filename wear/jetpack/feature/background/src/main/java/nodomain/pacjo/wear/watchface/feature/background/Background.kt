package nodomain.pacjo.wear.watchface.feature.background

import android.util.Log
import nodomain.pacjo.wear.watchface.base.feature.FeatureOption
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext

/**
 * The contract for a specific background.
 * It is a FeatureOption, so it has an ID and name, and it can draw a preview.
 * It also knows how to draw the actual background on the watch face.
 */
abstract class Background : FeatureOption {
    abstract fun draw(renderingContext: RenderingContext)

    override fun drawPreview(renderingContext: RenderingContext) {
        Log.d(TAG, "preview called")

        draw(renderingContext)
    }

    companion object {
        const val TAG = "Background"
    }
}