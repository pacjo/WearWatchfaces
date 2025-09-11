package nodomain.pacjo.wear.watchface.base.renderer

import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext

/**
 * An interface defining the drawing contract for a watch face.
 */
interface WatchFaceRenderer {
    /**
     * Main rendering method called for each frame.
     *
     * @param renderingContext context containing the appropriate backend and current time.
     */
    fun draw(renderingContext: RenderingContext) { }
}