package nodomain.pacjo.wear.watchface

import StringMorpher
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.Typeface
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
import nodomain.pacjo.wear.watchface.utils.AnimatedClockType
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
//            drawMorphingClockV2(canvas, bounds, zonedDateTime)
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
            timePaint.textSize /= 1.5f
            drawTextCentredBoth(canvas, timePaint, "$hours:$minutes", bounds.width() / 2f, bounds.height() / 2.85f)
        } else {
            timePaint.textSize /= 1.5f
            drawTextCentredBoth(canvas, timePaint, "$hours:$minutes", bounds.width() / 2f, bounds.height() / 2f)
        }
    }

    private fun drawScrollingClock(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        if (zonedDateTime.nano <= 1_000_000_000 - TIME_TRANSITION_MS * 1_000_000) {
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
            AnimatedClockType.HOURS_24
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
            AnimatedClockType.MINUTES
        )

        drawScrollingFragment(
            canvas,
            zonedDateTime,
            timePaint,
            bounds.width() * 0.8f,
            bounds.height() / 2.25f,
            drawProperties.secondsOffsetScale,
            AnimatedClockType.SECONDS
        )
    }

    private fun getGlyphPaths(text: String, paint: Paint): List<Path> {
        val paths = mutableListOf<Path>()

        for (character in text) {
            val path = Path()

            paint.getTextPath(character.toString(), 0, 1, 0f, 0f, path)
            paths.add(path)
        }

        return paths
    }

    private fun normalizePaths(paths: List<Path>, numPoints: Int): List<Path> {
        return paths.map { path ->
            val measure = PathMeasure(path, false)
            val coords = FloatArray(2)
            val normalizedPath = Path()

            for (i in 0 until numPoints) {
                measure.getPosTan(measure.length * i / (numPoints - 1), coords, null)
                if (i == 0) {
                    normalizedPath.moveTo(coords[0], coords[1])
                } else {
                    normalizedPath.lineTo(coords[0], coords[1])
                }
            }
            normalizedPath
        }
    }

    private fun getGlyphPath(text: String, paint: Paint): Path {
        val path = Path()

        paint.getTextPath(text, 0, 1, 0f, 0f, path)

        return path
    }

    private fun normalizePath(path: Path, numPoints: Int): Path {
        val measure = PathMeasure(path, false)
        val coords = FloatArray(2)

        val normalizedPath = Path()

        for (i in 0 until numPoints) {
            measure.getPosTan(measure.length * i / (numPoints - 1), coords, null)
            if (i == 0) {
                normalizedPath.moveTo(coords[0], coords[1])
            } else {
                normalizedPath.lineTo(coords[0], coords[1])
            }
        }

        return normalizedPath
    }

    private fun morphPath(startPath: Path, endPath: Path, progress: Float): Path {
        val morphedPath = Path()

        // Measure the start path
        val startMeasure = PathMeasure(startPath, false)
        val endMeasure = PathMeasure(endPath, false)

        // Get the number of points to sample along each path
        val numPoints = 100
        val startCoords = FloatArray(2)
        val endCoords = FloatArray(2)

        for (i in 0 until numPoints) {
            val t = i / (numPoints - 1f)
            startMeasure.getPosTan(startMeasure.length * t, startCoords, null)
            endMeasure.getPosTan(endMeasure.length * t, endCoords, null)

            // Interpolate the coordinates
            val x = startCoords[0] + progress * (endCoords[0] - startCoords[0])
            val y = startCoords[1] + progress * (endCoords[1] - startCoords[1])

            if (i == 0) {
                morphedPath.moveTo(x, y)
            } else {
                morphedPath.lineTo(x, y)
            }
        }

        return morphedPath
    }

    private fun morphPaths(startPaths: List<Path>, endPaths: List<Path>, progress: Float): List<Path> {
        val morphedPaths = mutableListOf<Path>()

        for (i in startPaths.indices) {
            val startPath = startPaths[i]
            val endPath = if (i < endPaths.size) endPaths[i] else Path() // Handle extra characters, TODO: do we need this?
            morphedPaths.add(morphPath(startPath, endPath, progress))
        }

        return morphedPaths
    }

    private fun drawMorphingClockFragment(
        canvas: Canvas,
        zonedDateTime: ZonedDateTime,
        paint: Paint,
        x: Float,
        y: Float,
        transitionScale: Float,
        type: AnimatedClockType
    ) {
        val currentText: String
        val nextText: String

        when (type) {
            AnimatedClockType.HOURS_12 -> TODO("implement (overall) 12 hour support")
            AnimatedClockType.HOURS_24 -> {
                // make sure we don't show 24
                currentText = zonedDateTime.hour.toString().padStart(2, '0')
                nextText = if (zonedDateTime.hour <= 23)
                    (zonedDateTime.hour + 1).toString().padStart(2, '0')
                else
                    "00"
            }

            AnimatedClockType.MINUTES -> {
                // make sure we don't show 60
                currentText = zonedDateTime.minute.toString().padStart(2, '0')
                nextText = if (zonedDateTime.minute <= 59)
                    (zonedDateTime.minute + 1).toString().padStart(2, '0')
                else
                    "00"
            }

            AnimatedClockType.SECONDS -> {
                // make sure we don't show 60
                currentText = zonedDateTime.second.toString().padStart(2, '0')
                nextText = if (zonedDateTime.second <= 59)
                    (zonedDateTime.second + 1).toString().padStart(2, '0')
                else
                    "00"
            }
        }

        val currentPaths = normalizePaths(getGlyphPaths(currentText, paint), 100)
        val nextPaths = normalizePaths(getGlyphPaths(nextText, paint), 100)

        val morphedPaths = morphPaths(currentPaths, nextPaths, transitionScale)

        canvas.save()

        // TODO: fix letter spacing, also this is probably broken for length > 2 (and it's of the assumption that both texts are the same length)
        canvas.translate(x, y)
        for (path in morphedPaths) {
            canvas.drawPath(path, paint)

            canvas.translate(paint.textSize / currentText.length + paint.textSize / 10, 0f)
        }

        canvas.restore()
    }

    private fun drawMorphingClock(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        // TODO: make general
        if (zonedDateTime.nano <= 1_000_000_000 - TIME_TRANSITION_MS * 1_000_000) {
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
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }

        // hours
        drawMorphingClockFragment(
            canvas,
            zonedDateTime,
            timePaint,
            bounds.width() * 0.05f,
            bounds.height() / 2f,
            drawProperties.hoursOffsetScale,
            AnimatedClockType.HOURS_24
        )

        // minutes and seconds
        timePaint.apply {
            textSize /= 2.5f
            strokeWidth = 3f        // TODO: why?! does it even change anything in the original?
        }

        drawMorphingClockFragment(
            canvas,
            zonedDateTime,
            timePaint,
            bounds.width() * 0.7f,
            bounds.height() / 3.15f,
            drawProperties.minutesOffsetScale,
            AnimatedClockType.MINUTES
        )

        drawMorphingClockFragment(
            canvas,
            zonedDateTime,
            timePaint,
            bounds.width() * 0.7f,
            bounds.height() / 2.00f,
            drawProperties.secondsOffsetScale,
            AnimatedClockType.SECONDS
        )
    }

    private fun drawMorphingClockV2(
        canvas: Canvas,
        bounds: Rect,
        zonedDateTime: ZonedDateTime
    ) {
        if (zonedDateTime.nano <= 1_000_000_000 - TIME_TRANSITION_MS * 1_000_000) {
            secondsChangeAnimation.start()

            if (zonedDateTime.second == 59) {
                minutesChangeAnimation.start()

                if (zonedDateTime.minute == 59)
                    hoursChangeAnimation.start()
            }
        }

        val maxTextWidth = bounds.width() / 2f
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
            style = Paint.Style.STROKE
            strokeWidth = 5f
        }

        // Hours
//        drawMorphingText(
//            canvas,
//            timePaint,
//            zonedDateTime.hour.toString().padStart(2, '0'),
//            ((zonedDateTime.hour + 1) % 24).toString().padStart(2, '0'),
//            bounds.width() * 0.05f,
//            bounds.height() / 2f,
//            drawProperties.hoursOffsetScale
//        )

        // TEMP: seconds but in place of hours, TODO: remove

        drawMorphingText(
            canvas,
            timePaint,
            zonedDateTime.second.toString().padStart(2, '0'),
            ((zonedDateTime.second + 1) % 60).toString().padStart(2, '0'),
            bounds.width() * 0.05f,
            bounds.height() / 2f,
            drawProperties.hoursOffsetScale
        )

        // Minutes and seconds
        timePaint.apply {
            textSize /= 2.5f
            strokeWidth = 3f
        }

        drawMorphingText(
            canvas,
            timePaint,
            zonedDateTime.minute.toString().padStart(2, '0'),
            ((zonedDateTime.minute + 1) % 60).toString().padStart(2, '0'),
            bounds.width() * 0.7f,
            bounds.height() / 3.15f,
            drawProperties.minutesOffsetScale
        )

        drawMorphingText(
            canvas,
            timePaint,
            zonedDateTime.second.toString().padStart(2, '0'),
            ((zonedDateTime.second + 1) % 60).toString().padStart(2, '0'),
            bounds.width() * 0.7f,
            bounds.height() / 2.00f,
            drawProperties.secondsOffsetScale
        )
    }

    private fun drawMorphingText(
        canvas: Canvas,
        paint: Paint,
        startText: String,
        endText: String,
        x: Float,
        y: Float,
        progress: Float
    ) {
        val morpher = StringMorpher(startText, endText, TIME_TRANSITION_MS)
        morpher.progress = progress
        morpher.paint.apply {
            textSize = paint.textSize
            isAntiAlias = true
            shader = paint.shader
            style = paint.style
            strokeWidth = paint.strokeWidth
        }
        canvas.save()
        canvas.translate(x, y)
        morpher.draw(canvas)
        canvas.restore()
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