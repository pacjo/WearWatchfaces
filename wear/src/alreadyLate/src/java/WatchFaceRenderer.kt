package nodomain.pacjo.wear.watchface

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.FloatProperty
import android.util.Log
import android.view.SurfaceHolder
import android.view.animation.AnimationUtils
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
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
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds
import nodomain.pacjo.wear.watchface.data.watchface.HandsStyles
import nodomain.pacjo.wear.watchface.data.watchface.WatchFaceColorPalette.Companion.convertToWatchFaceColorPalette
import nodomain.pacjo.wear.watchface.data.watchface.WatchFaceData
import nodomain.pacjo.wear.watchface.utils.COLOR_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.ComplicationConfig
import nodomain.pacjo.wear.watchface.utils.DRAW_COMPLICATIONS_IN_AMBIENT_SETTING
import nodomain.pacjo.wear.watchface.utils.HANDS_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.SMOOTH_SECONDS_HAND_SETTING
import nodomain.pacjo.wear.watchface.utils.drawComplications
import java.time.ZonedDateTime
import kotlin.math.min

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
    private var drawProperties = DrawProperties()

    private val ambientExitAnimator =
        AnimatorSet().apply {
            val interpolation =
                AnimationUtils.loadInterpolator(
                    context,
                    android.R.interpolator.accelerate_decelerate
                )
            playTogether(
                ObjectAnimator.ofFloat(drawProperties, DrawProperties.HANDS_SCALE, 1.0f)
                    .apply {
                        duration = HANDS_ANIMATION_MS
                        interpolator = interpolation
                        setAutoCancel(true)
                    }
            )
        }

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
        // Listen for ambient state changes.
        scope.launch {
            watchState.isAmbient.collect { isAmbient ->
                if (!isAmbient!!) {
                    ambientExitAnimator.start()
                }
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
                        activeColorStyle = ColorStyleIdAndResourceIds.getColorStyleConfig(
                            listOption.id.toString()
                        )
                    )
                }
                HANDS_STYLE_SETTING -> {
                    val listOption = options.value as
                            UserStyleSetting.ListUserStyleSetting.ListOption

                    // TODO: check if this can stay
                    newWatchFaceData = newWatchFaceData.copy(
                        handsStyle = HandsStyles.getHandsStyleConfig(
                            listOption.id.toString()
                        )
                    )
                }
                DRAW_COMPLICATIONS_IN_AMBIENT_SETTING -> {
                    val booleanValue = options.value as
                        UserStyleSetting.BooleanUserStyleSetting.BooleanOption

                    newWatchFaceData = newWatchFaceData.copy(
                        drawComplicationsInAmbient = booleanValue.value
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
        drawBackground(canvas, bounds)
        // TODO: disable animations properly, or just do something else with this
        drawProperties.handsScale = 1f

        if (renderParameters.drawMode != DrawMode.AMBIENT || (renderParameters.drawMode == DrawMode.AMBIENT && watchFaceData.drawComplicationsInAmbient)) {
            drawComplicationsBackground(canvas, bounds)
            drawComplications(canvas, zonedDateTime, renderParameters, complicationSlotsManager)
        }

        drawHands(canvas, bounds, zonedDateTime)
    }

    override fun renderHighlightLayer(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime,
        sharedAssets: SimpleSharedAssets
    ) {
        canvas.drawColor(renderParameters.highlightLayer!!.backgroundTint)

        for ((_, complication) in complicationSlotsManager.complicationSlots) {
            if (complication.enabled) {
                complication.renderHighlightLayer(canvas, zonedDateTime, renderParameters)
            }
        }

        if (renderParameters.highlightLayer!!.highlightedElement ==
            RenderParameters.HighlightedElement.UserStyle(UserStyleSetting.Id(HANDS_STYLE_SETTING))
            ) {
            drawHands(canvas, bounds, zonedDateTime)
        }
    }

    private fun drawHands(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        // TODO: don't stretch image
        val hoursDegrees = ((zonedDateTime.hour + zonedDateTime.minute / 60f) / 12f) * 360 * drawProperties.handsScale
        watchFaceData.handsStyle.hourHandDrawFunction?.invoke(canvas, bounds, renderParameters, watchFaceColors, hoursDegrees)      // TODO: allow use drawable instead

        val minutesDegrees = ((zonedDateTime.minute + zonedDateTime.second / 60f) / 60f) * 360 * drawProperties.handsScale
        watchFaceData.handsStyle.minuteHandDrawFunction?.invoke(canvas, bounds, renderParameters, watchFaceColors, minutesDegrees)      // TODO: allow use drawable instead

        if (renderParameters.drawMode != DrawMode.AMBIENT) {
            val secondsDegrees = when (watchFaceData.smoothSecondsHand) {
                false ->  zonedDateTime.second / 60f * 360
                else -> ((zonedDateTime.second + zonedDateTime.nano / 1000000000f) / 60f) * 360
            } * drawProperties.handsScale

            watchFaceData.handsStyle.secondHandDrawFunction?.invoke(canvas, bounds, renderParameters, watchFaceColors, secondsDegrees)      // TODO: allow use drawable instead
        }
    }

    private fun drawBackground(
        canvas: Canvas,
        bounds: Rect
    ) {
        canvas.drawColor(watchFaceColors.backgroundColor)

        val vectorBackground = VectorDrawableCompat.create(context.resources, R.drawable.background, null)
        vectorBackground?.bounds = bounds
        vectorBackground?.draw(canvas)
    }

    private fun drawComplicationsBackground(canvas: Canvas, bounds: Rect) {
        val highlightPaint = Paint().apply {
            isAntiAlias = true
            color = watchFaceColors.ambientTertiaryColor
            alpha = 150
        }

        canvas.drawRoundRect(
            ComplicationConfig.Right.bounds.left * bounds.width().toFloat(),
            ComplicationConfig.Right.bounds.top * bounds.height().toFloat(),
            ComplicationConfig.Right.bounds.right * bounds.width().toFloat(),
            ComplicationConfig.Right.bounds.bottom * bounds.height().toFloat(),
            COMPLICATIONS_BACKGROUND_CORNER_RADIUS,
            COMPLICATIONS_BACKGROUND_CORNER_RADIUS,
            highlightPaint
        )
    }

    private class DrawProperties(
        var handsScale: Float = 0f
    ) {
        companion object {
            val HANDS_SCALE =
                object : FloatProperty<DrawProperties>("handsScale") {
                    override fun setValue(obj: DrawProperties, value: Float) {
                        obj.handsScale = value
                    }

                    override fun get(obj: DrawProperties): Float {
                        return obj.handsScale
                    }
                }
        }
    }

    companion object {
        private const val TAG = "WatchCanvasRenderer"

        private const val HANDS_ANIMATION_MS = 600L

        private const val COMPLICATIONS_BACKGROUND_CORNER_RADIUS = 40f
    }
}
