package nodomain.pacjo.wear.watchface

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.util.FloatProperty
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds
import nodomain.pacjo.wear.watchface.data.watchface.WatchFaceColorPalette.Companion.convertToWatchFaceColorPalette
import nodomain.pacjo.wear.watchface.data.watchface.WatchFaceData
import nodomain.pacjo.wear.watchface.utils.COLOR_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.ComplicationConfig
import nodomain.pacjo.wear.watchface.utils.DRAW_COMPLICATIONS_IN_AMBIENT_SETTING
import nodomain.pacjo.wear.watchface.utils.drawComplications
import nodomain.pacjo.wear.watchface.utils.drawScrollingFragment
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

    private val secondsChangeAnimation =
        ObjectAnimator.ofFloat(drawProperties, DrawProperties.SECONDS_OFFSET_SCALE, 0f, 1f)
            .apply {
                duration = TIME_TRANSITION_MS
                interpolator =
                    AnimationUtils.loadInterpolator(
                        context,
                        android.R.interpolator.accelerate_decelerate
                    )

                setAutoCancel(true)
            }

    private val minutesChangeAnimation =
        ObjectAnimator.ofFloat(drawProperties, DrawProperties.MINUTES_OFFSET_SCALE, 0f, 1f)
            .apply {
                duration = TIME_TRANSITION_MS
                interpolator =
                    AnimationUtils.loadInterpolator(
                        context,
                        android.R.interpolator.accelerate_decelerate
                    )

                setAutoCancel(true)

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // Reset the property to the starting value
                        drawProperties.minutesOffsetScale = 0f
                    }
                })
            }

    private val hoursChangeAnimation =
        ObjectAnimator.ofFloat(drawProperties, DrawProperties.HOURS_OFFSET_SCALE, 0f, 1f)
            .apply {
                duration = TIME_TRANSITION_MS
                interpolator =
                    AnimationUtils.loadInterpolator(
                        context,
                        android.R.interpolator.accelerate_decelerate
                    )

                setAutoCancel(true)

                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        // Reset the property to the starting value
                        drawProperties.hoursOffsetScale = 0f
                    }
                })
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
                DRAW_COMPLICATIONS_IN_AMBIENT_SETTING -> {
                    val booleanValue = options.value as
                            UserStyleSetting.BooleanUserStyleSetting.BooleanOption

                    newWatchFaceData = newWatchFaceData.copy(
                        drawComplicationsInAmbient = booleanValue.value
                    )
                }
            }
        }

        // Only update if something changed.
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
        canvas.drawColor(watchFaceColors.backgroundColor)

        if (renderParameters.drawMode != DrawMode.AMBIENT || (renderParameters.drawMode == DrawMode.AMBIENT && watchFaceData.drawComplicationsInAmbient)) {
            drawComplications(canvas, zonedDateTime, renderParameters, complicationSlotsManager)
            drawComplicationsBackground(canvas, bounds)
        }

        if (renderParameters.drawMode != DrawMode.AMBIENT)
            drawScrollingClock(canvas, bounds, zonedDateTime)
        else
            drawClock(canvas, bounds, zonedDateTime)
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

    private fun drawClock(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        val maxTextWidth = bounds.width() / 2f      // TODO: fix
        val maxTextSize = maxTextWidth * 1.1f

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
                        bounds.width().toFloat(),
                        bounds.height() / 2f,
                        watchFaceColors.activePrimaryColor,
                        watchFaceColors.activeSecondaryColor,
                        Shader.TileMode.CLAMP
                    )
                }
            }
        }

        val hours = zonedDateTime.hour.toString().padStart(2, '0')
        val minutes = zonedDateTime.minute.toString().padStart(2, '0')
        val seconds = zonedDateTime.second.toString().padStart(2, '0')

        // draw time
        if (renderParameters.drawMode != DrawMode.AMBIENT) {
            val timeXSpacing = bounds.width() * 0.03f
            drawTextCentredVertically(canvas, timePaint, hours, timeXSpacing, bounds.height() / 2.85f)

            timePaint.apply {
                textSize /= 2.5f
                strokeWidth = 2f
            }
            drawTextCentredVertically(canvas, timePaint, minutes, timeXSpacing + maxTextWidth + bounds.width() * 0.175f, bounds.height() / 2.85f - timePaint.textSize / 2)
            drawTextCentredVertically(canvas, timePaint, seconds, timeXSpacing + maxTextWidth + bounds.width() * 0.175f, bounds.height() / 2.85f + timePaint.textSize / 2)
        } else if (watchFaceData.drawComplicationsInAmbient) {
            // TODO: add animation
            timePaint.textSize /= 1.5f
            drawTextCentredBoth(canvas, timePaint, "$hours:$minutes", bounds.width() / 2f, bounds.height() / 2.85f)
        } else {
            timePaint.textSize /= 1.5f
            drawTextCentredBoth(canvas, timePaint, "$hours:$minutes", bounds.width() / 2f, bounds.height() / 2f)
        }
    }

    enum class ScrollingClockType {
        SECONDS,
        MINUTES,
        HOURS_12,
        HOURS_24
    }

    private fun drawScrollingClock(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        // we multiply by 0.8 to slightly delay the transition,
        // so it when it finishes, we won't start another animation,
        // leading to visual glitches
        if (zonedDateTime.nano <= 1_000_000_000 - TIME_TRANSITION_MS * 1_000_000 * 0.8) {
            secondsChangeAnimation.start()

            if (zonedDateTime.second == 59) {
                minutesChangeAnimation.start()

                if (zonedDateTime.minute == 59)
                    hoursChangeAnimation.start()
            }
        }

        val maxTextWidth = bounds.width() / 2f      // TODO: fix
        val maxTextSize = maxTextWidth * 1.1f

        val timePaint = Paint().apply {
            textSize = maxTextSize
            isAntiAlias = true
            shader = LinearGradient(
                0f,
                0f,
                bounds.width().toFloat(),
                bounds.height() / 2f,
                watchFaceColors.activePrimaryColor,
                watchFaceColors.activeSecondaryColor,
                Shader.TileMode.CLAMP
            )
        }

        // hours
        drawScrollingFragment(
            canvas,
            zonedDateTime,
            timePaint,
            bounds.width() * 0.35f,
            bounds.height() / 2.85f,
            drawProperties.hoursOffsetScale,
            ScrollingClockType.HOURS_24
        )

        // minutes and seconds
        timePaint.apply {
            textSize /= 2.5f
            strokeWidth = 2f
        }

        drawScrollingFragment(
            canvas,
            zonedDateTime,
            timePaint,
            bounds.width() * 0.8f,
            bounds.height() / 3.75f,
            drawProperties.minutesOffsetScale,
            ScrollingClockType.MINUTES
        )

        drawScrollingFragment(
            canvas,
            zonedDateTime,
            timePaint,
            bounds.width() * 0.8f,
            bounds.height() / 2.25f,
            drawProperties.secondsOffsetScale,
            ScrollingClockType.SECONDS
        )
    }

    private fun drawComplicationsBackground(canvas: Canvas, bounds: Rect) {
        val highlightPaint = Paint().apply {
            isAntiAlias = true
            color = when (renderParameters.drawMode) {
                DrawMode.AMBIENT -> watchFaceColors.ambientTertiaryColor
                else -> watchFaceColors.activeTertiaryColor
            }
            alpha = 75
        }

        canvas.drawRoundRect(
            ComplicationConfig.LeftOuter.bounds.left * bounds.width(),
            ComplicationConfig.LeftOuter.bounds.top * bounds.height(),
            ComplicationConfig.RightOuter.bounds.right * bounds.width(),
            ComplicationConfig.RightOuter.bounds.bottom * bounds.height(),
            COMPLICATIONS_BACKGROUND_CORNER_RADIUS,
            COMPLICATIONS_BACKGROUND_CORNER_RADIUS,
            highlightPaint
        )
    }

    private class DrawProperties(
        var secondsOffsetScale: Float = 0f,
        var minutesOffsetScale: Float = 0f,
        var hoursOffsetScale: Float = 0f
    ) {
        companion object {
            val SECONDS_OFFSET_SCALE =
                object : FloatProperty<DrawProperties>("secondsOffsetScale") {
                    override fun setValue(obj: DrawProperties, value: Float) {
                        obj.secondsOffsetScale = value
                    }

                    override fun get(obj: DrawProperties): Float {
                        return obj.secondsOffsetScale
                    }
                }
            val MINUTES_OFFSET_SCALE =
                object : FloatProperty<DrawProperties>("minutesOffsetScale") {
                    override fun setValue(obj: DrawProperties, value: Float) {
                        obj.minutesOffsetScale = value
                    }

                    override fun get(obj: DrawProperties): Float {
                        return obj.minutesOffsetScale
                    }
                }
            val HOURS_OFFSET_SCALE =
                object : FloatProperty<DrawProperties>("hoursOffsetScale") {
                    override fun setValue(obj: DrawProperties, value: Float) {
                        obj.hoursOffsetScale = value
                    }

                    override fun get(obj: DrawProperties): Float {
                        return obj.hoursOffsetScale
                    }
                }
        }
    }

    companion object {
        private const val TAG = "WatchCanvasRenderer"

        private const val COMPLICATIONS_BACKGROUND_CORNER_RADIUS = 40f

        private const val TIME_TRANSITION_MS = 350L
    }
}