package nodomain.pacjo.wear.watchface.base.renderer

import android.graphics.Canvas
import android.graphics.Rect
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import nodomain.pacjo.wear.watchface.base.feature.DrawableFeature
import nodomain.pacjo.wear.watchface.base.feature.WatchFaceFeature
import java.time.ZonedDateTime

private const val FRAME_PERIOD_MS_DEFAULT: Long = 16L

class CanvasRendererAdapter(
    private val renderer: WatchFaceRenderer,
    features: List<WatchFaceFeature>,
    surfaceHolder: SurfaceHolder,
    currentUserStyleRepository: CurrentUserStyleRepository,
    watchState: WatchState,
    canvasType: Int = CanvasType.HARDWARE
) : Renderer.CanvasRenderer2<CanvasRendererAdapter.SimpleSharedAssets>(
    surfaceHolder = surfaceHolder,
    currentUserStyleRepository = currentUserStyleRepository,
    watchState = watchState,
    canvasType = canvasType,
    interactiveDrawModeUpdateDelayMillis = FRAME_PERIOD_MS_DEFAULT,
    clearWithBackgroundTintBeforeRenderingHighlightLayer = false
) {

    class SimpleSharedAssets: SharedAssets {
        override fun onDestroy() { }
    }

    override suspend fun createSharedAssets(): SimpleSharedAssets {
        return SimpleSharedAssets()
    }

    private val drawableFeatures: List<DrawableFeature> by lazy {
        features.filterIsInstance<DrawableFeature>().sortedBy { it.layer.ordinal }
    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: SimpleSharedAssets
    ) {
        // create Canvas backend for this frame
        val canvasBackend = CanvasBackendImpl(canvas, bounds)
        val context = RenderingContext(canvasBackend, zonedDateTime, renderParameters)

        // draw features in layer order
        drawableFeatures.forEach { feature ->
            feature.draw(context)
        }

        // other drawing
        renderer.draw(context)
    }

    override fun renderHighlightLayer(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: SimpleSharedAssets
    ) {
        // TODO: implement
    }

    private data class CanvasBackendImpl(
        override val canvas: Canvas,
        override val bounds: Rect,
    ) : CanvasRendererBackend
}