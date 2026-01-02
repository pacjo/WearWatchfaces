package nodomain.pacjo.wear.watchface.feature.overlay

import android.util.Log
import nodomain.pacjo.wear.watchface.feature.base.FeatureOption
import nodomain.pacjo.wear.watchface.shared.RenderingContext

/**
 * The contract for a specific overlay.
 * It is a FeatureOption, so it has an ID and name, and it can draw a preview.
 * It also knows how to draw the actual overlay on the watch face.
 */
abstract class Overlay : FeatureOption {
    abstract fun draw(renderingContext: RenderingContext)

    override fun drawPreview(renderingContext: RenderingContext) {
        Log.d(TAG, "preview called")

        draw(renderingContext)
    }

    companion object {
        const val TAG = "Overlay"
    }
}