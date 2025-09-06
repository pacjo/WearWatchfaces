package nodomain.pacjo.wear.watchface.base.renderer

import android.view.SurfaceHolder
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import nodomain.pacjo.wear.watchface.base.feature.DrawableFeature
import nodomain.pacjo.wear.watchface.base.feature.GranularWatchFaceLayer
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
        features.filterIsInstance<DrawableFeature>()
    }

    override fun render(
        zonedDateTime: ZonedDateTime,
        sharedAssets: SimpleSharedAssets
    ) {
        // create OpenGL backend for this frame
        val openGLBackend = OpenGLBackendImpl

        // draw watchface in layer order
        GranularWatchFaceLayer.entries.forEach { layer ->
            val context = RenderingContext(openGLBackend, zonedDateTime, renderParameters, layer)
            drawableFeatures.filter { it.layer == layer }.forEach { feature ->
                feature.draw(context)
            }

            // this *will* by called multiple times (once per layer), so it's important to
            // handle this in the WatchFaceRenderer.draw(context) implementations
            renderer.draw(context)
        }
    }

    override fun renderHighlightLayer(
        zonedDateTime: ZonedDateTime,
        sharedAssets: SimpleSharedAssets
    ) {
        // TODO: implement
    }

    private data object OpenGLBackendImpl : OpenGLRendererBackend
}
