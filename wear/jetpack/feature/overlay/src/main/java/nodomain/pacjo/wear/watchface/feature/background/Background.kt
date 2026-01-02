package nodomain.pacjo.wear.watchface.feature.background

import android.util.Log
import nodomain.pacjo.wear.watchface.feature.overlay.Overlay
import nodomain.pacjo.wear.watchface.shared.RenderingContext

/**
 * Very light wrapper around [Overlay], changing only the class tag for logger.
 *
 * Exists mostly to keep backgrounds and overlays somewhat separate,
 * even though they are mostly the same thing.
 */
abstract class Background : Overlay() {
    override fun drawPreview(renderingContext: RenderingContext) {
        Log.d(TAG, "preview called")

        draw(renderingContext)
    }

    companion object {
        const val TAG = "Background"
    }
}