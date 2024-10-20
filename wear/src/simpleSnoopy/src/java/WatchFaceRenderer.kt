package nodomain.pacjo.wear.watchface

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.Log
import android.view.SurfaceHolder
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyle
import androidx.wear.watchface.style.UserStyleSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import nodomain.pacjo.wear.watchface.data.watchface.BackgroundStyles
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyle
import nodomain.pacjo.wear.watchface.data.watchface.HandsStyles
import nodomain.pacjo.wear.watchface.data.watchface.WatchFaceColorPalette.Companion.convertToWatchFaceColorPalette
import nodomain.pacjo.wear.watchface.data.watchface.WatchFaceData
import nodomain.pacjo.wear.watchface.utils.BACKGROUND_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.COLOR_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.HANDS_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.SMOOTH_SECONDS_HAND_SETTING
import nodomain.pacjo.wear.watchface.utils.drawBackground
import java.time.ZonedDateTime

// Default for how long each frame is displayed at expected frame rate.
private const val FRAME_PERIOD_MS_DEFAULT: Long = 16L

class WatchCanvasRenderer(
    private val context: Context,
    surfaceHolder: SurfaceHolder,
    watchState: WatchState,
    private val complicationSlotsManager: ComplicationSlotsManager,
    currentUserStyleRepository: CurrentUserStyleRepository,
    canvasType: Int
): Renderer.CanvasRenderer2<WatchCanvasRenderer.SimpleSharedAssets>(
    surfaceHolder,
    currentUserStyleRepository,
    watchState,
    canvasType,
    FRAME_PERIOD_MS_DEFAULT,
    clearWithBackgroundTintBeforeRenderingHighlightLayer = false
) {
    class SimpleSharedAssets: SharedAssets {
        override fun onDestroy() { }
    }

    private val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private var watchFaceData: WatchFaceData = WatchFaceData()

    private var watchFaceColors = convertToWatchFaceColorPalette(
        context,
        watchFaceData.activeColorStyle,
        watchFaceData.ambientColorStyle
    )

    init {
        scope.launch {
            currentUserStyleRepository.userStyle.collect { userStyle ->
                updateWatchFaceData(userStyle)
            }
        }
    }

    override suspend fun createSharedAssets(): SimpleSharedAssets {
        return SimpleSharedAssets()
    }

    private fun updateWatchFaceData(userStyle: UserStyle) {
        Log.d(TAG, "updateWatchFace(): $userStyle")

        var newWatchFaceData: WatchFaceData = watchFaceData

        // Loops through user style and applies new values to watchFaceData.
        for (options in userStyle) {
            when (options.key.id.toString()) {
                COLOR_STYLE_SETTING -> {
                    val listOption = options.value as
                        UserStyleSetting.ListUserStyleSetting.ListOption

                    newWatchFaceData = newWatchFaceData.copy(
                        activeColorStyle = ColorStyle.getColorStyleConfig(
                            listOption.id.toString()
                        )
                    )
                }
                HANDS_STYLE_SETTING -> {
                    val listOption = options.value as
                            UserStyleSetting.ListUserStyleSetting.ListOption

                    newWatchFaceData = newWatchFaceData.copy(
                        handsStyle = HandsStyles.getHandsStyleConfig(
                            listOption.id.toString()
                        )
                    )
                }
                BACKGROUND_STYLE_SETTING -> {
                    val listOption = options.value as
                            UserStyleSetting.ListUserStyleSetting.ListOption

                    newWatchFaceData = newWatchFaceData.copy(
                        backgroundStyle = BackgroundStyles.getBackgroundStyleConfig(
                            listOption.id.toString()
                        )
                    )
                }
                SMOOTH_SECONDS_HAND_SETTING -> {
                    val booleanValue = options.value as
                        UserStyleSetting.BooleanUserStyleSetting.BooleanOption

                    newWatchFaceData = newWatchFaceData.copy(
                        smoothSecondsHand = booleanValue.value
                    )
                }
            }
        }

        // Only updates if something changed.
        if (watchFaceData != newWatchFaceData) {
            watchFaceData = newWatchFaceData

            watchFaceColors = convertToWatchFaceColorPalette(
                context,
                watchFaceData.activeColorStyle,
                watchFaceData.ambientColorStyle
            )
        }
    }

    override fun render(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: SimpleSharedAssets
    ) {
        drawBackground(context, watchFaceData, canvas, bounds)

        drawHands(canvas, bounds, zonedDateTime)
    }

    override fun renderHighlightLayer(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: SimpleSharedAssets
    ) {
        canvas.drawColor(renderParameters.highlightLayer!!.backgroundTint)

        if (renderParameters.highlightLayer!!.highlightedElement ==
            RenderParameters.HighlightedElement.UserStyle(UserStyleSetting.Id(HANDS_STYLE_SETTING))
        ) {
            drawHands(canvas, bounds, zonedDateTime)
        } else if (renderParameters.highlightLayer!!.highlightedElement ==
            RenderParameters.HighlightedElement.UserStyle(UserStyleSetting.Id(BACKGROUND_STYLE_SETTING))
        ) {
            drawBackground(context, watchFaceData, canvas, bounds)
        }
    }

    private fun drawHands(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        // TODO: don't stretch image
        val hoursDegrees = ((zonedDateTime.hour + zonedDateTime.minute / 60f) / 12f) * 360
        watchFaceData.handsStyle.hourHandDrawFunction?.invoke(canvas, bounds, renderParameters, watchFaceColors, hoursDegrees)      // TODO: allow use drawable instead

        val minutesDegrees = ((zonedDateTime.minute + zonedDateTime.second / 60f) / 60f) * 360
        watchFaceData.handsStyle.minuteHandDrawFunction?.invoke(canvas, bounds, renderParameters, watchFaceColors, minutesDegrees)      // TODO: allow use drawable instead

        if (renderParameters.drawMode != DrawMode.AMBIENT) {
            val secondsDegrees = when (watchFaceData.smoothSecondsHand) {
                false ->  zonedDateTime.second / 60f * 360
                else -> ((zonedDateTime.second + zonedDateTime.nano / 1000000000f) / 60f) * 360
            }

            watchFaceData.handsStyle.secondHandDrawFunction?.invoke(canvas, bounds, renderParameters, watchFaceColors, secondsDegrees)      // TODO: allow use drawable instead
        }
    }

    companion object {
        private const val TAG = "WatchCanvasRenderer"
    }
}