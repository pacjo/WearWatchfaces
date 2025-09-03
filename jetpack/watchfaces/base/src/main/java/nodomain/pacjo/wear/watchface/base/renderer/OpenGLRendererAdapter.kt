package nodomain.pacjo.wear.watchface.base.renderer

import android.view.SurfaceHolder
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import nodomain.pacjo.wear.watchface.base.feature.DrawableFeature
import nodomain.pacjo.wear.watchface.base.feature.WatchFaceFeature
import java.time.ZonedDateTime

private const val FRAME_PERIOD_MS_DEFAULT: Long = 16L

class OpenGLRendererAdapter(
    private val renderer: WatchFaceRenderer,
    private val features: List<WatchFaceFeature>,
    surfaceHolder: SurfaceHolder,
    currentUserStyleRepository: CurrentUserStyleRepository,
    watchState: WatchState,
) : Renderer.GlesRenderer2<OpenGLRendererAdapter.SimpleSharedAssets>(
    surfaceHolder = surfaceHolder,
    currentUserStyleRepository = currentUserStyleRepository,
    watchState = watchState,
    interactiveDrawModeUpdateDelayMillis = FRAME_PERIOD_MS_DEFAULT
) {

    class SimpleSharedAssets: SharedAssets {
        override fun onDestroy() {
            // TODO: Add opengl resources
        }
    }

    override suspend fun createSharedAssets(): SimpleSharedAssets {
        return SimpleSharedAssets()
    }

    private val drawableFeatures: List<DrawableFeature> by lazy {
        features.filterIsInstance<DrawableFeature>().sortedBy { it.layer.ordinal }
    }

    override fun render(
        zonedDateTime: ZonedDateTime,
        sharedAssets: SimpleSharedAssets
    ) {
        // create OpenGL backend for this frame
        val openGLBackend = OpenGLBackendImpl(zonedDateTime)
        val context = RenderingContext(openGLBackend, zonedDateTime, renderParameters)

        // draw features in layer order
        drawableFeatures.forEach { feature ->
            feature.draw(context)
        }

        // other drawing
        renderer.draw(context)
    }

    override fun renderHighlightLayer(
        zonedDateTime: ZonedDateTime,
        sharedAssets: SimpleSharedAssets
    ) {
        // TODO: implement
    }

    private data class OpenGLBackendImpl(
        override val zonedDateTime: ZonedDateTime
    ) : OpenGLRendererBackend
}
