package nodomain.pacjo.wear.watchface.base_analog

import android.graphics.Canvas
import android.graphics.Rect
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import nodomain.pacjo.wear.watchface.base.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.feature.base.DrawableFeature
import nodomain.pacjo.wear.watchface.feature.base.WatchFaceFeature
import java.time.ZonedDateTime

// Default for how long each frame is displayed at expected frame rate.
private const val FRAME_PERIOD_MS_DEFAULT: Long = 16L       // TODO: should it be here or in the interface?

/**
 * A generic Renderer that delegates its drawing calls to a specific
 * implementation of a [nodomain.pacjo.wear.watchface.base.WatchFaceRenderer].
 */
// TODO: maybe unify with DigitalRendererAdapter once present
class AnalogRendererAdapter(
    private val renderer: WatchFaceRenderer,
    features: List<WatchFaceFeature>,
    surfaceHolder: SurfaceHolder,
    currentUserStyleRepository: CurrentUserStyleRepository,
    watchState: WatchState,
    canvasType: Int = CanvasType.HARDWARE
) : Renderer.CanvasRenderer2<AnalogRendererAdapter.SimpleSharedAssets>(
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

    override fun render(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, sharedAssets: SimpleSharedAssets) {
        renderer.drawBackground(canvas, bounds, zonedDateTime)
        renderer.drawComplications(canvas, bounds, zonedDateTime)

//        renderer.drawClock(canvas, bounds, zonedDateTime)     // TODO: unused in analog

        // TODO: would be nice if we could draw something (or just get a callback before/after layers)
        drawableFeatures.forEach { feature ->
            feature.draw(canvas, bounds, zonedDateTime)
        }
    }

    override fun renderHighlightLayer(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, sharedAssets: SimpleSharedAssets) {
        // TODO: figure out how we want to use this
    }
}