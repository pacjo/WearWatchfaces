package nodomain.pacjo.wear.watchface.base_analog

import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSchema
import kotlinx.coroutines.CoroutineScope
import nodomain.pacjo.wear.watchface.base.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import kotlin.coroutines.coroutineContext

// TODO: move what's possible to base
abstract class AnalogWatchFaceService : WatchFaceService() {
    abstract fun getFeatureFactories(): List<FeatureFactory>

    abstract fun createWatchFaceRenderer(): WatchFaceRenderer

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        val coroutineScope = CoroutineScope(coroutineContext)

        // 1. Ask the concrete class for its desired feature factories.
        val featureFactories = getFeatureFactories()

        // 2. Create the features by calling each factory.
        // This loop is the heart of the new design. The base service doesn't
        // know or care what a "HandStyleFeature" is.
        val features = featureFactories.map { factory ->
            factory.create(this, coroutineScope, currentUserStyleRepository)
        }

        // 3. Build the schema from all the created features.
        // TODO: not sure about this
        val styleSettings = features.flatMap { feature -> feature.getStyleSettings(applicationContext) }
        val userStyleRepository = CurrentUserStyleRepository(UserStyleSchema(styleSettings))

        val renderer = AnalogRendererAdapter(
            surfaceHolder = surfaceHolder,
            watchState = watchState,
            renderer = createWatchFaceRenderer(),
            features = features,
//            complicationSlotsManager = complicationSlotsManager,
            currentUserStyleRepository = userStyleRepository,       // TODO: was currentUserStyleRepository
            canvasType = CanvasType.HARDWARE
        )

        val watchFace = WatchFace(
            watchFaceType = WatchFaceType.ANALOG,
            renderer = renderer
        )

        return watchFace
    }
}