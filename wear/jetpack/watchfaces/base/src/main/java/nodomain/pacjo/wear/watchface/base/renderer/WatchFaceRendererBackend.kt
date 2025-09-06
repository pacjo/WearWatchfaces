package nodomain.pacjo.wear.watchface.base.renderer

import android.graphics.Canvas
import android.graphics.Rect
import androidx.wear.watchface.RenderParameters
import nodomain.pacjo.wear.watchface.base.feature.GranularWatchFaceLayer
import java.time.ZonedDateTime

/**
 * Abstract interface defining the contract for watch face renderer backends.
 */
sealed interface WatchFaceRendererBackend

/**
 * Backend for Canvas-based rendering operations.
 */
interface CanvasRendererBackend : WatchFaceRendererBackend {
    /** The Canvas instance for drawing operations */
    val canvas: Canvas

    /** The rectangular bounds of the drawing area */
    val bounds: Rect
}

/**
 * Backend for OpenGL ES-based rendering operations.
 */
interface OpenGLRendererBackend : WatchFaceRendererBackend

/**
 * A rendering context that provides access to the appropriate backend for drawing operations.
 */
data class RenderingContext(
    val backend: WatchFaceRendererBackend,
    val zonedDateTime: ZonedDateTime,
    val renderParameters: RenderParameters,
    val layer: GranularWatchFaceLayer
) {

    /**
     * Executes the given block if this context uses a Canvas rendering backend.
     *
     * This method provides access to the full [CanvasRendererBackend] object, which includes
     * the Canvas, Bounds, and ZonedDateTime.
     *
     * @param block block to execute with the Canvas backend.
     */
    inline fun ifCanvas(block: (CanvasRendererBackend) -> Unit) {
        if (backend is CanvasRendererBackend) {
            block(backend)
        }
    }

    /**
     * Executes the given block if this context uses a Canvas rendering backend.
     *
     * @param block The block to execute with Canvas drawing parameters. The block receives:
     *              - canvas: The Canvas instance for drawing
     *              - bounds: The rectangular drawing area
     *              - zonedDateTime: The current time for this frame
     */
    inline fun ifCanvas(block: (Canvas, Rect, ZonedDateTime) -> Unit) {
        if (backend is CanvasRendererBackend) {
            block(backend.canvas, backend.bounds, zonedDateTime)
        }
    }

    /**
     * Executes the given block if this context uses an OpenGL rendering backend.
     *
     * @param block block to execute with the OpenGL backend.
     */
    inline fun ifOpenGL(block: (OpenGLRendererBackend) -> Unit) {
        if (backend is OpenGLRendererBackend) {
            block(backend)
        }
    }

    val isCanvas: Boolean get() = backend is CanvasRendererBackend

    val isOpenGL: Boolean get() = backend is OpenGLRendererBackend
}
