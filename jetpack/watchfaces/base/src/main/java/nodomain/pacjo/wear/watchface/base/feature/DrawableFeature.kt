package nodomain.pacjo.wear.watchface.base.feature

import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext

/**
 * An interface for any WatchFaceFeature that needs to draw content on the watch face. Features
 * implementing this interface can adapt their drawing logic based on the available rendering
 * backend.
 *
 * ## Integration with Adapters
 *
 * DrawableFeatures are automatically handled by both:
 * - [nodomain.pacjo.wear.watchface.base.renderer.CanvasRendererAdapter] for Canvas-based watch faces
 * - [nodomain.pacjo.wear.watchface.base.renderer.OpenGLRendererAdapter] for OpenGL-based watch faces
 *
 * @see RenderingContext
 * @see WatchFaceLayer
 * @see WatchFaceFeature
 */
interface DrawableFeature : WatchFaceFeature {

    /**
     * The layer on which this feature should be drawn.
     *
     * @see WatchFaceLayer
     */
    val layer: WatchFaceLayer

    /**
     * This method is called during each frame render cycle and should contain all drawing logic
     * for the feature. It receives a [RenderingContext] that provides access to the
     * appropriate rendering backend (Canvas or OpenGL) and the current time.
     *
     * @param renderingContext context containing the appropriate backend and current time.
     *
     * @see RenderingContext
     * @see RenderingContext.ifCanvas
     * @see RenderingContext.ifOpenGL
     */
    fun draw(renderingContext: RenderingContext)
}