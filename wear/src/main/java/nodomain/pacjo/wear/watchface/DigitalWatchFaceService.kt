package nodomain.pacjo.wear.watchface

import android.graphics.Rect
import android.os.Build
import android.view.SurfaceHolder
import androidx.annotation.RequiresApi
import androidx.wear.watchface.CanvasComplicationFactory
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.ComplicationSlot
import androidx.wear.watchface.ComplicationSlotBoundsType
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.ComplicationTapFilter
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

class DigitalWatchFaceService : WatchFaceService() {
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
            when (config.type) {
                ComplicationSlotBoundsType.ROUND_RECT -> ComplicationSlot.createRoundRectComplicationSlotBuilder(
                    id = config.id,
                    canvasComplicationFactory = defaultCanvasComplicationFactory,
                    supportedTypes = config.supportedTypes,
                    defaultDataSourcePolicy = config.defaultDataSourcePolicy,
                    bounds = ComplicationSlotBounds(config.bounds)
                ) .build()

                ComplicationSlotBoundsType.EDGE -> ComplicationSlot.createEdgeComplicationSlotBuilder(
                    id = config.id,
                    canvasComplicationFactory = defaultCanvasComplicationFactory,
                    supportedTypes = config.supportedTypes,
                    defaultDataSourcePolicy = config.defaultDataSourcePolicy,
                    bounds = ComplicationSlotBounds(config.bounds),
                    complicationTapFilter = object : ComplicationTapFilter {
                        override fun hitTest(
                            complicationSlot: ComplicationSlot,
                            screenBounds: Rect,
                            x: Int,
                            y: Int,
                            includeMargins: Boolean
                        ): Boolean {
                            // TODO: change, it can't stay like this
                            return false
                        }
                    }
                ).build()

                ComplicationSlotBoundsType.BACKGROUND -> ComplicationSlot.createBackgroundComplicationSlotBuilder(
                    id = config.id,
                    canvasComplicationFactory = defaultCanvasComplicationFactory,
                    supportedTypes = config.supportedTypes,
                    defaultDataSourcePolicy = config.defaultDataSourcePolicy,
                ).build()

                else -> throw IllegalArgumentException("${config.type} not in list of possible values: ComplicationSlotBoundsType.(ROUND_RECT/EDGE/BACKGROUND)")
            }

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

        val watchFace = WatchFace(
            watchFaceType = WatchFaceType.DIGITAL,
            renderer = renderer
        )

        // not every renderer implements TapListener
        if (renderer is WatchFace.TapListener)
            watchFace.setTapListener(renderer)

        return watchFace
    }

    companion object {
        const val TAG = "DigitalWatchFaceService"
    }
}