package nodomain.pacjo.wear.watchface

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
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
import nodomain.pacjo.wear.watchface.utils.drawTextCentredBoth
import nodomain.pacjo.wear.watchface.utils.drawTextCentredVertically
import java.time.ZonedDateTime

// Default for how long each frame is displayed at expected frame rate.
private const val FRAME_PERIOD_MS_DEFAULT: Long = 32L

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

    // Represents all data needed to render the watch face. All value defaults are constants.
    // Those changeable by user are saved in the watch face APIs and we update those here
    // (in the renderer) through a Kotlin Flow.
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

        if (renderParameters.drawMode != DrawMode.AMBIENT || (renderParameters.drawMode == DrawMode.AMBIENT && watchFaceData.drawComplicationsInAmbient)) {
            drawComplications(canvas, zonedDateTime)        // TODO: probably should be reversed
            drawComplicationsBackground(canvas, bounds)
        }

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

    // ----- All drawing functions -----
    private fun drawComplications(canvas: Canvas, zonedDateTime: ZonedDateTime) {
        for ((_, complication) in complicationSlotsManager.complicationSlots) {
            if (complication.enabled) {
                complication.render(canvas, zonedDateTime, renderParameters)
            }
        }
    }

    private fun drawClock(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        val maxTextWidth = bounds.width() / 2f
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
                        bounds.width().toFloat(),   // it should be `/ maxTextWidth` but current looks better
                        bounds.height() / 2f,
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
        private const val TAG = "WatchCanvasRenderer"

        private const val MODE_TRANSITION_MS = 500L

        private const val COMPLICATIONS_BACKGROUND_CORNER_RADIUS = 40f
    }
}