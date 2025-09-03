package nodomain.pacjo.wear.watchface.base

import android.view.SurfaceHolder
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceTypeIntDef
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSchema
import kotlinx.coroutines.CoroutineScope
import nodomain.pacjo.wear.watchface.base.renderer.CanvasRendererAdapter
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.base.feature.FeatureFactory
import nodomain.pacjo.wear.watchface.base.feature.complications.Complications
import kotlin.coroutines.coroutineContext

abstract class BaseWatchFaceService : WatchFaceService() {
    @WatchFaceTypeIntDef abstract val watchFaceType: Int
    abstract fun getFeatureFactories(): List<FeatureFactory>
    abstract fun createWatchFaceRenderer(): WatchFaceRenderer

    final override fun createUserStyleSchema(): UserStyleSchema {
        val featureFactories = getFeatureFactories()

        // use lightweight factory method to get all settings
        val settings = featureFactories.flatMap { factory ->
            factory.getStyleSettings(this)
        }

        return UserStyleSchema(settings)
    }

    final override fun createComplicationSlotsManager(
        currentUserStyleRepository: CurrentUserStyleRepository
    ): ComplicationSlotsManager {
        // ignored if no ComplicationsFeature factory is present
        return Complications.createComplicationSlotsManager(
            context = this,
            featureFactories = getFeatureFactories(),
            currentUserStyleRepository = currentUserStyleRepository
        )
    }

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        val featureFactories = getFeatureFactories()

        // create proper/full features using the now available dependencies
        val features = featureFactories.map { factory ->
            factory.create(this, CoroutineScope(coroutineContext), currentUserStyleRepository, watchState)
        }

        // connect each complications feature with ComplicationSlotsManager
        Complications.connectComplicationFeatures(features, complicationSlotsManager)

        val renderer = CanvasRendererAdapter(
            renderer = createWatchFaceRenderer(),
            features = features,
            surfaceHolder = surfaceHolder,
            currentUserStyleRepository = currentUserStyleRepository,
            watchState = watchState
        )

        return WatchFace(watchFaceType, renderer)
    }
}