package nodomain.pacjo.wear.watchface

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.util.FloatProperty
import android.util.IntProperty
import android.util.Log
import android.view.SurfaceHolder
import android.view.animation.AnimationUtils
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyle
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds
import nodomain.pacjo.wear.watchface.data.watchface.WatchFaceColorPalette.Companion.convertToWatchFaceColorPalette
import nodomain.pacjo.wear.watchface.data.watchface.WatchFaceData
import nodomain.pacjo.wear.watchface.utils.COLOR_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.DRAW_COMPLICATIONS_IN_AMBIENT_SETTING
import nodomain.pacjo.wear.watchface.utils.TIME_RING_CORNER_RADIUS_SETTING
import nodomain.pacjo.wear.watchface.utils.TIME_RING_WIDTH_SETTING
import nodomain.pacjo.wear.watchface.utils.drawTextCentredBoth
import nodomain.pacjo.wear.watchface.utils.drawTextCentredVertically
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
    private var drawProperties = DrawProperties()

    private val ambientExitAnimator =
        AnimatorSet().apply {
            val linearOutSlow =
                AnimationUtils.loadInterpolator(
                    context,
                    android.R.interpolator.accelerate_decelerate
                )
            playTogether(
                ObjectAnimator.ofInt(drawProperties, DrawProperties.TRANSPARENCY_SCALE, 255)
                    .apply {
                        duration = MODE_TRANSITION_MS
                        interpolator = linearOutSlow
                        setAutoCancel(true)
                    },
                ObjectAnimator.ofFloat(drawProperties, DrawProperties.SECONDS_SCALE, 1.0f)
                .apply {
                    duration = TIME_RING_TRANSITION_MS
                    interpolator = linearOutSlow
                    setAutoCancel(true)
                }
            )
        }

    // Animation played when entering ambient mode.
    private val ambientEnterAnimator =
        AnimatorSet().apply {
            val fastOutLinearIn =
                AnimationUtils.loadInterpolator(
                    context,
                    android.R.interpolator.fast_out_linear_in
                )
            playTogether(
                ObjectAnimator.ofInt(drawProperties, DrawProperties.TRANSPARENCY_SCALE, 255)
                    .apply {
                        duration = MODE_TRANSITION_MS
                        interpolator = fastOutLinearIn
                        setAutoCancel(true)
                    },
                ObjectAnimator.ofFloat(drawProperties, DrawProperties.SECONDS_SCALE, 0.0f)
                    .apply {
                        duration = TIME_RING_TRANSITION_MS
                        interpolator = fastOutLinearIn
                        setAutoCancel(true)
                    }
            )
        }

    class SimpleSharedAssets: SharedAssets {
        override fun onDestroy() {
            // TODO: why is this empty?
        }
    }

    private val scope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    // Represents all data needed to render the watch face. All value defaults are constants. Only
    // three values are changeable by the user (color scheme, ticks being rendered, and length of
    // the minute arm). Those dynamic values are saved in the watch face APIs and we update those
    // here (in the renderer) through a Kotlin Flow.
    private var watchFaceData: WatchFaceData = WatchFaceData()

    // Converts resource ids into Colors and ComplicationDrawable.
    private var watchFaceColors = convertToWatchFaceColorPalette(
        context,
        watchFaceData.activeColorStyle,
        watchFaceData.ambientColorStyle
    )

    // TODO: more code goes here

    init {
        scope.launch {
            currentUserStyleRepository.userStyle.collect { userStyle ->
                updateWatchFaceData(userStyle)
            }
        }
        // Listen for ambient state changes.
        scope.launch {
            watchState.isAmbient.collect { isAmbient ->
                if (isAmbient!!) {
                    ambientEnterAnimator.start()
                } else {
                    ambientExitAnimator.start()
                }
            }
        }
    }

//    override fun shouldAnimate(): Boolean {
//        // Make sure we keep animating while ambientEnterAnimator is running.
//        return ambientEnterAnimator.isRunning || super.shouldAnimate()
//    }

    override suspend fun createSharedAssets(): SimpleSharedAssets {
        return SimpleSharedAssets()
    }

    private fun updateWatchFaceData(userStyle: UserStyle) {
        Log.d(TAG, "updateWatchFace(): $userStyle")

        Log.i("pacjodebug", "updateWatchFaceData: userStyle: $userStyle")

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
                TIME_RING_WIDTH_SETTING -> {
                    val floatValue = options.value as
                            UserStyleSetting.DoubleRangeUserStyleSetting.DoubleRangeOption

                    newWatchFaceData = newWatchFaceData.copy(
                        timeRingWidth = floatValue.value.toFloat()
                    )
                }
                TIME_RING_CORNER_RADIUS_SETTING -> {
                    val floatValue = options.value as
                            UserStyleSetting.DoubleRangeUserStyleSetting.DoubleRangeOption

                    newWatchFaceData = newWatchFaceData.copy(
                        timeRingCornerRadius = floatValue.value.toFloat()
                    )
                }
                DRAW_COMPLICATIONS_IN_AMBIENT_SETTING -> {
                    val booleanValue = options.value as
                            UserStyleSetting.BooleanUserStyleSetting.BooleanOption

                    newWatchFaceData = newWatchFaceData.copy(
                        drawComplicationsInAmbient = booleanValue.value
                    )
                }
            }
        }

        // Only updates if something changed.
        if (watchFaceData != newWatchFaceData) {
            watchFaceData = newWatchFaceData

            // Recreates Color and ComplicationDrawable from resource ids.
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
        canvas.drawColor(watchFaceColors.backgroundColor)

        if (renderParameters.drawMode != DrawMode.AMBIENT) {
            drawTimeRing(canvas, bounds, zonedDateTime)
        }

        if (renderParameters.drawMode != DrawMode.AMBIENT || (renderParameters.drawMode == DrawMode.AMBIENT && watchFaceData.drawComplicationsInAmbient)) {
            drawComplications(canvas, zonedDateTime)
        }

        if (renderParameters.watchFaceLayers.contains(WatchFaceLayer.COMPLICATIONS_OVERLAY)) {
            drawClock(canvas, bounds, zonedDateTime)
        }
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
    }

    // ----- All drawing functions -----
    private fun drawComplications(canvas: Canvas, zonedDateTime: ZonedDateTime) {
        for ((_, complication) in complicationSlotsManager.complicationSlots) {
            if (complication.enabled) {
                complication.render(canvas, zonedDateTime, renderParameters)
            }
        }
    }

    private fun drawTimeRing(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        val ringPaint = Paint().apply {
            color = watchFaceColors.activeTertiaryColor
            alpha = 100
            isAntiAlias = true
        }

        val borderRect = RectF(
            bounds.left + watchFaceData.timeRingWidth / 2,
            bounds.top + watchFaceData.timeRingWidth / 2,
            bounds.right - watchFaceData.timeRingWidth / 2,
            bounds.bottom - watchFaceData.timeRingWidth / 2
        )

        canvas.drawRoundRect(
            bounds.left.toFloat(),
            bounds.top.toFloat(),
            bounds.right.toFloat(),
            bounds.bottom.toFloat(),
            watchFaceData.timeRingCornerRadius,
            watchFaceData.timeRingCornerRadius,
            ringPaint
        )

        canvas.drawRoundRect(
            borderRect,
            watchFaceData.timeRingCornerRadius,
            watchFaceData.timeRingCornerRadius,
            Paint().apply {
                color = watchFaceColors.backgroundColor
                isAntiAlias = true
            }
        )

        // avoid flickering is we animate seconds close to 60
        val currentSeconds = (zonedDateTime.second + zonedDateTime.nano/1000000000f) * (if (zonedDateTime.second + 2 < 60) drawProperties.secondsScale else 1f)

        val startAngle = -90f // Start angle for the arc (12 o'clock position)
        val sweepAngle = (60 - currentSeconds * drawProperties.secondsScale) / 60f * 360f // Sweep angle based on seconds (0-60 as 0-100%)

        canvas.drawArc(
            RectF(bounds.right * -1f, bounds.bottom * -1f, bounds.right * 2f, bounds.bottom * 2f),
            startAngle,
            -sweepAngle,
            true,
            Paint().apply {
                color = watchFaceColors.backgroundColor
                isAntiAlias = true
            }
        )
    }

    private fun drawClock(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        val maxTextWidth = bounds.width() * (2f / 3f)
        val maxTextSize = maxTextWidth * 0.75f

        val timePaint = Paint().apply {
            textSize = maxTextSize
            isAntiAlias = true
            when (renderParameters.drawMode) {
                DrawMode.AMBIENT -> {
                    color = watchFaceColors.ambientPrimaryColor
                    style = Paint.Style.STROKE
                    strokeWidth = 4f
                }
                else -> {
                    shader = LinearGradient(
                        0f,
                        0f,
                        bounds.width()
                            .toFloat(),   // it should be `/ maxTextWidth` but current looks better
                        bounds.height().toFloat(),
                        watchFaceColors.activePrimaryColor,
                        watchFaceColors.activeSecondaryColor,
                        Shader.TileMode.CLAMP
                    )
                }
            }
//            alpha = drawProperties.transparencyScale
        }

        val hours = zonedDateTime.hour.toString().padStart(2, '0')
        val minutes = zonedDateTime.minute.toString().padStart(2, '0')

        // draw time
        if (renderParameters.drawMode == DrawMode.AMBIENT && !watchFaceData.drawComplicationsInAmbient) {
            val centerTimeXSpacing = bounds.width() * 0.5f
            drawTextCentredBoth(canvas, timePaint, hours, centerTimeXSpacing, bounds.centerY() / 2f + bounds.height() * 0.03f)
            drawTextCentredBoth(canvas, timePaint, minutes, centerTimeXSpacing, bounds.centerY() * (3/2f) - bounds.height() * 0.03f)
        } else {
            val timeXSpacing = bounds.width() * 0.04f
            drawTextCentredVertically(canvas, timePaint, hours, timeXSpacing, bounds.centerY() / 2f + bounds.height() * 0.03f)
            drawTextCentredVertically(canvas, timePaint, minutes, timeXSpacing, bounds.centerY() * (3/2f) - bounds.height() * 0.03f)
        }
    }

    private class DrawProperties(
        var transparencyScale: Int = 0,
        var secondsScale: Float = 0f
    ) {
        companion object {
            val TRANSPARENCY_SCALE =
                object : IntProperty<DrawProperties>("transparencyScale") {
                    override fun setValue(obj: DrawProperties, value: Int) {
                        obj.transparencyScale = value
                    }

                    override fun get(obj: DrawProperties): Int {
                        return obj.transparencyScale
                    }
                }
            val SECONDS_SCALE =
                object : FloatProperty<DrawProperties>("secondsScale") {
                    override fun setValue(obj: DrawProperties, value: Float) {
                        obj.secondsScale = value
                    }

                    override fun get(obj: DrawProperties): Float {
                        return obj.secondsScale
                    }
                }
        }
    }

    companion object {
        private const val TAG = "SimpleWatchCanvasRenderer"

        private const val TIME_RING_TRANSITION_MS = 750L
        private const val MODE_TRANSITION_MS = 500L
    }
}
