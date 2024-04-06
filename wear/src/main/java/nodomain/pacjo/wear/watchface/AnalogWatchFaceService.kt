package nodomain.pacjo.wear.watchface

import android.os.Build
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import androidx.wear.watchface.CanvasComplicationFactory
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.ComplicationSlot
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.complications.ComplicationSlotBounds
import androidx.wear.watchface.complications.rendering.CanvasComplicationDrawable
import androidx.wear.watchface.complications.rendering.ComplicationDrawable
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSchema
import nodomain.pacjo.wear.watchface.utils.ComplicationConfig
import nodomain.pacjo.wear.watchface.utils.createUserStyleSchema

class AnalogWatchFaceService : WatchFaceService(){
    override fun createUserStyleSchema(): UserStyleSchema =
        createUserStyleSchema(context = applicationContext)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun createComplicationSlotsManager(
        currentUserStyleRepository: CurrentUserStyleRepository
    ): ComplicationSlotsManager {
        val defaultCanvasComplicationFactory =
            CanvasComplicationFactory { watchState, listener ->
                CanvasComplicationDrawable(
                    ComplicationDrawable(this),
                    watchState,
                    listener
                )
            }

        val complicationSlots = ComplicationConfig.getAll<ComplicationConfig>().map { config ->
            ComplicationSlot.createRoundRectComplicationSlotBuilder(
                id = config.id,
                canvasComplicationFactory = defaultCanvasComplicationFactory,
                supportedTypes = config.supportedTypes,
                defaultDataSourcePolicy = config.defaultDataSourcePolicy,
                bounds = ComplicationSlotBounds(config.bounds)
            ).build()
        }

        return ComplicationSlotsManager(
            complicationSlots,
            currentUserStyleRepository
        )
    }

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        val renderer = WatchCanvasRenderer(
            context = applicationContext,
            surfaceHolder = surfaceHolder,
            watchState = watchState,
            complicationSlotsManager = complicationSlotsManager,
            currentUserStyleRepository = currentUserStyleRepository,
            canvasType = CanvasType.HARDWARE
        )

        return WatchFace(
            watchFaceType = WatchFaceType.ANALOG,
            renderer = renderer
        )
    }

    companion object {
        const val TAG = "AnalogWatchFaceService"
    }
}