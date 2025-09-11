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
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationsFeature
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.feature.colors.ColorAware
import nodomain.pacjo.wear.watchface.feature.colors.di.colorModule
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.coroutines.coroutineContext

abstract class BaseWatchFaceService : WatchFaceService() {

    override fun onCreate() {
        super.onCreate()
        if (GlobalContext.getOrNull() == null)
            startKoin {
                androidContext(this@BaseWatchFaceService)
                modules(colorModule)            // TODO: remove from here
            }
    }

    override fun onDestroy() {
        stopKoin()
        super.onDestroy()
    }

    @WatchFaceTypeIntDef abstract val watchFaceType: Int
    abstract fun getFeatureFactories(): List<FeatureFactory>
    open fun createWatchFaceRenderer(): WatchFaceRenderer {
        // empty renderer by default, since it's possible to make a watchface using only features
        return object : WatchFaceRenderer { }
    }

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
        return ComplicationsFeature.createComplicationSlotsManager(
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
        val coroutineScope = CoroutineScope(coroutineContext)
        getKoin().declare(coroutineScope)

        // create proper/full features using the now available dependencies
        val features = getFeatureFactories().map { factory ->
            factory.create(this, coroutineScope, currentUserStyleRepository, watchState)
        }

        // color features - TODO: remove from here and make a system to set up features like this and complications
        features.filterIsInstance<ColorAware>().forEach { source ->
            getKoin().declare(source)
        }

        // connect each complications feature with ComplicationSlotsManager
        ComplicationsFeature.connectComplicationFeatures(features, complicationSlotsManager)

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