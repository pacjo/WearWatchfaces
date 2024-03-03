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
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.SystemDataSources
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.rendering.CanvasComplicationDrawable
import androidx.wear.watchface.complications.rendering.ComplicationDrawable
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSchema
import nodomain.pacjo.wear.watchface.utils.ComplicationConfig
import nodomain.pacjo.wear.watchface.utils.createUserStyleSchema

class SimpleWatchFaceService : WatchFaceService(){
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

        val topLeftComplicationSlot = ComplicationSlot.createRoundRectComplicationSlotBuilder(
            id = ComplicationConfig.TopLeft.id,
            canvasComplicationFactory = defaultCanvasComplicationFactory,
            supportedTypes = ComplicationConfig.TopLeft.supportedTypes,
            defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                SystemDataSources.DATA_SOURCE_DATE,
                ComplicationType.SHORT_TEXT
            ),
            bounds = ComplicationSlotBounds(ComplicationConfig.TopLeft.bounds)
        ).build()

        val topRightComplicationSlot = ComplicationSlot.createRoundRectComplicationSlotBuilder(
            id = ComplicationConfig.TopRight.id,
            canvasComplicationFactory = defaultCanvasComplicationFactory,
            supportedTypes = ComplicationConfig.TopRight.supportedTypes,
            defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                SystemDataSources.DATA_SOURCE_WATCH_BATTERY,
                ComplicationType.RANGED_VALUE
            ),
            bounds = ComplicationSlotBounds(ComplicationConfig.TopRight.bounds)
        ).build()

        val middleComplicationSlot = ComplicationSlot.createRoundRectComplicationSlotBuilder(
            id = ComplicationConfig.Middle.id,
            canvasComplicationFactory = defaultCanvasComplicationFactory,
            supportedTypes = ComplicationConfig.Middle.supportedTypes,
            defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                SystemDataSources.DATA_SOURCE_STEP_COUNT,
                ComplicationType.SHORT_TEXT
            ),
            bounds = ComplicationSlotBounds(ComplicationConfig.Middle.bounds)
        ).build()

        val bottomComplicationSlot = ComplicationSlot.createRoundRectComplicationSlotBuilder(
            id = ComplicationConfig.Bottom.id,
            canvasComplicationFactory = defaultCanvasComplicationFactory,
            supportedTypes = ComplicationConfig.Bottom.supportedTypes,
            defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                SystemDataSources.DATA_SOURCE_NEXT_EVENT,
                ComplicationType.LONG_TEXT
            ),
            bounds = ComplicationSlotBounds(ComplicationConfig.Bottom.bounds)
        ).build()

        return ComplicationSlotsManager(
            listOf(topLeftComplicationSlot, topRightComplicationSlot, middleComplicationSlot, bottomComplicationSlot),
            currentUserStyleRepository
        )
    }

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        val renderer = SimpleWatchCanvasRenderer(
            context = applicationContext,
            surfaceHolder = surfaceHolder,
            watchState = watchState,
            complicationSlotsManager = complicationSlotsManager,
            currentUserStyleRepository = currentUserStyleRepository,
            canvasType = CanvasType.HARDWARE
        )

        return WatchFace(
            watchFaceType = WatchFaceType.DIGITAL,
            renderer = renderer
        )
    }

    companion object {
        const val TAG = "SimpleWatchFaceService"
    }
}