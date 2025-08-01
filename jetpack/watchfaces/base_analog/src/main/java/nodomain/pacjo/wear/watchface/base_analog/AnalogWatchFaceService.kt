package nodomain.pacjo.wear.watchface.base_analog

import android.view.SurfaceHolder
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

    final override fun createUserStyleSchema(): UserStyleSchema {
        val featureFactories = getFeatureFactories()

        // Use the new lightweight factory method to get all settings
        val allSettings = featureFactories.flatMap { factory ->
            factory.getStyleSettings(this) // 'this' is the context
        }

        return UserStyleSchema(allSettings)
    }

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        // 1. Ask the concrete class for its desired feature factories.
        val featureFactories = getFeatureFactories()

        // 2. Create the FULL features using the now-available dependencies
        val features = featureFactories.map { factory ->
            factory.create(this, CoroutineScope(coroutineContext), currentUserStyleRepository, watchState)
        }

        // 3. Create the renderer, passing it the list of all active features
        val customRenderer = createWatchFaceRenderer()
        val renderer = AnalogRendererAdapter(
            customRenderer,
            features,
            surfaceHolder,
            currentUserStyleRepository,
            watchState
        )

        return WatchFace(WatchFaceType.ANALOG, renderer)
    }
}