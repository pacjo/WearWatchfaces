package nodomain.pacjo.wear.watchface.feature.base

import nodomain.pacjo.wear.watchface.shared.GranularWatchFaceLayer
import nodomain.pacjo.wear.watchface.shared.RenderingContext

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
 * @see nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
 * @see nodomain.pacjo.wear.watchface.base.GranularWatchFaceLayer
 * @see nodomain.pacjo.wear.watchface.feature.base.WatchFaceFeature
 */
interface DrawableFeature : WatchFaceFeature {

    /**
     * The layer on which this feature should be drawn.
     *
     * @see nodomain.pacjo.wear.watchface.base.GranularWatchFaceLayer
     */
    val layer: GranularWatchFaceLayer

    /**
     * This method is called during each frame render cycle and should contain all drawing logic
     * for the feature. It receives a [nodomain.pacjo.wear.watchface.base.renderer.RenderingContext] that provides access to the
     * appropriate rendering backend (Canvas or OpenGL) and the current time.
     *
     * @param renderingContext context containing the appropriate backend and current time.
     *
     * @see nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
     * @see nodomain.pacjo.wear.watchface.base.renderer.RenderingContext.ifCanvas
     * @see nodomain.pacjo.wear.watchface.base.renderer.RenderingContext.ifOpenGL
     */
    fun draw(renderingContext: RenderingContext)
}