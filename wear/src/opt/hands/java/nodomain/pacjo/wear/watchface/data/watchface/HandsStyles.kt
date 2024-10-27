package nodomain.pacjo.wear.watchface.data.watchface

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Icon
import androidx.annotation.StringRes
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting
import nodomain.pacjo.wear.watchface.R
import kotlin.math.min

private const val HAND_WIDTH_SIMPLE = 5f
private const val HAND_WIDTH_SECOND = 2f
private const val HAND_WIDTH_STOLEN = 6f
private const val CIRCLE_RADIUS = 2f

private const val HAND_FILL_ALPHA = 50      // TODO: use this
private const val HAND_ROUND_RADIUS = 6f

private const val STOLEN_HANDS_COLOR = Color.WHITE

/**
 * Represents watch face hands style options that user can select.
 *
 * Since I don't want to create preview images for each style manually,
 * we just generate them on the fly (and reuse them in [HandsStyleSelectScreen]).
 */

private fun createPaint(color: Int, style: Paint.Style, strokeWidth: Float = 0f): Paint {
    return Paint().apply {
        isAntiAlias = true
        this.color = color
        this.style = style
        this.strokeWidth = strokeWidth
    }
}

private fun drawSimpleHand(
    canvas: Canvas,
    bounds: Rect,
    centerSpacing: Float,
    length: Float,
    width: Float,
    color: Int,
    angle: Float,
    isFilled: Boolean = true
) {
    val handPaint = createPaint(
        color = color,
        style = if (isFilled) Paint.Style.FILL else Paint.Style.STROKE,
        strokeWidth = if (!isFilled) 3f else 0f
    )

    canvas.rotate(angle, bounds.centerX().toFloat(), bounds.centerY().toFloat())

    val widthFraction = width / (if (isFilled) 2f else 2.5f)

    canvas.drawRoundRect(
        bounds.centerX().toFloat() - widthFraction,
        bounds.centerY().toFloat() - widthFraction - length,
        bounds.centerX().toFloat() + widthFraction,
        bounds.centerY().toFloat() + widthFraction - centerSpacing,
        width,
        width,
        handPaint
    )

    // TODO: don't know if we need it
    canvas.rotate(-angle, bounds.centerX().toFloat(), bounds.centerY().toFloat())
}

private fun drawStolenHand(
    canvas: Canvas,
    bounds: Rect,
    length: Float,
    angle: Float,
    drawStandoff: Boolean = true,
    fillMode: HandFillMode = HandFillMode.DARKENED
) {
    // Create paint for the outline
    val outlinePaint = createPaint(Color.BLACK, Paint.Style.STROKE, 3f)
    outlinePaint.strokeJoin = Paint.Join.ROUND
    outlinePaint.strokeCap = Paint.Cap.ROUND

    val standoffLength = length / 6f  // TODO: make const

    // Rotate canvas around the center of bounds
    canvas.rotate(
        angle,
        bounds.centerX().toFloat(),
        bounds.centerY().toFloat()
    )

    // Create path for the hand shape
    val handPath = Path()

    // Calculate standoff position
    val standoffX = bounds.centerX().toFloat()
    val standoffY = bounds.centerY() - standoffLength

    // Move to standoff position
    handPath.moveTo(standoffX, standoffY)

    // Draw the line to the hand position if drawStandoff is true
    if (drawStandoff)
        handPath.lineTo(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat()
        )

    // Add round rectangle for the hand shape
    handPath.addRoundRect(
        RectF(
            bounds.centerX() - HAND_WIDTH_STOLEN,
            bounds.centerY() - length,
            bounds.centerX() + HAND_WIDTH_STOLEN,
            bounds.centerY() - standoffLength
        ),
        HAND_ROUND_RADIUS,
        HAND_ROUND_RADIUS,
        Path.Direction.CW
    )

    // Draw the filled area of the hand shape if fillMode is not NONE
    if (fillMode != HandFillMode.NONE) {
        val fillPaint = createPaint(STOLEN_HANDS_COLOR, Paint.Style.FILL, HAND_WIDTH_STOLEN)
        if (fillMode == HandFillMode.DARKENED) {
            fillPaint.alpha = 50
        }
        canvas.drawPath(handPath, fillPaint)
    }

    // Draw the outline of the hand shape
    canvas.drawPath(handPath, outlinePaint)

    // Draw the circle at the hand position
    if (drawStandoff) {
        canvas.drawCircle(
            bounds.centerX().toFloat(),
            bounds.centerY().toFloat(),
            outlinePaint.strokeWidth,
            createPaint(Color.GRAY, Paint.Style.FILL)
        )
    }

    // Rotate canvas back to its original orientation
    canvas.rotate(-angle, bounds.centerX().toFloat(), bounds.centerY().toFloat())
}

fun drawSecondsHandStandoff(
    canvas: Canvas,
    bounds: Rect,
    drawMode: DrawMode,
    watchFaceColors: WatchFaceColorPalette,
    radius: Float = CIRCLE_RADIUS
) {
    canvas.drawCircle(
        bounds.centerX().toFloat(),
        bounds.centerY().toFloat(),
        radius,
        createPaint(
            color = 
                if (drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientTertiaryColor
                else
                    watchFaceColors.activeTertiaryColor,
            style = Paint.Style.STROKE,
            strokeWidth = HAND_WIDTH_SECOND
        )
    )
}

enum class HandFillMode {
    NONE,
    DARKENED,
    SOLID
}

enum class HandsStyles(
    val id: String,
    @StringRes val nameResourceId: Int,
    val hourHandDrawFunction: ((Canvas, Rect, DrawMode, WatchFaceColorPalette, Float) -> Unit)? = null,
    val minuteHandDrawFunction: ((Canvas, Rect, DrawMode, WatchFaceColorPalette, Float) -> Unit)? = null,
    val secondHandDrawFunction: ((Canvas, Rect, DrawMode, WatchFaceColorPalette, Float) -> Unit)? = null
) {
    MODERN(
        id = "style1_hands_id",
        nameResourceId = R.string.hands_style1_name,
        hourHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * 0.06f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.6f,
                width = HAND_WIDTH_SIMPLE * 2f,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientPrimaryColor
                    else
                        watchFaceColors.activePrimaryColor,
                angle = angle
            )
        },
        minuteHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * 0.08f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.7f,
                width = HAND_WIDTH_SIMPLE,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientSecondaryColor
                    else
                        watchFaceColors.activeSecondaryColor,
                angle = angle
            )
        },
        secondHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * 0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.9f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSecondsHandStandoff(canvas, bounds, drawMode, watchFaceColors)
        }
    ),

    BOLD(
        id = "style2_hands_id",
        nameResourceId = R.string.hands_style2_name,
        hourHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = 0f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.5f,
                width = HAND_WIDTH_SIMPLE * 6f,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientPrimaryColor
                    else
                        watchFaceColors.activePrimaryColor,
                angle = angle
            )
        },
        minuteHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = 0f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.85f,
                width = HAND_WIDTH_SIMPLE * 1.5f,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientSecondaryColor
                    else
                        watchFaceColors.activeSecondaryColor,
                angle = angle
            )
        },
        secondHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * 0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.9f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSecondsHandStandoff(canvas, bounds, drawMode, watchFaceColors)
        }
    ),

    BOLD_OUTLINE(
        id = "style3_hands_id",
        nameResourceId = R.string.hands_style3_name,
        hourHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = 0f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.5f,
                width = HAND_WIDTH_SIMPLE * 8f,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientPrimaryColor
                    else
                        watchFaceColors.activePrimaryColor,
                angle = angle,
                false
            )
        },
        minuteHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = 0f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.85f,
                width = HAND_WIDTH_SIMPLE * 1.5f,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientSecondaryColor
                    else
                        watchFaceColors.activeSecondaryColor,
                angle = angle
            )
        },
        secondHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * 0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.9f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSecondsHandStandoff(canvas, bounds, drawMode, watchFaceColors)
        }
    ),

    STOLEN(
        id = "style4_hands_id",
        nameResourceId = R.string.hands_style4_name,
        hourHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas = canvas,
                bounds = bounds,
                length = min(bounds.width(), bounds.height()) / 2 * 0.5f,
                angle = angle,
                fillMode = HandFillMode.DARKENED
            )
        },
        minuteHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas = canvas,
                bounds = bounds,
                length = min(bounds.width(), bounds.height()) / 2 * 0.7f,
                angle = angle,
                fillMode = HandFillMode.DARKENED
            )
        },
        secondHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * 0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.9f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * -0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * -0.1f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSecondsHandStandoff(canvas, bounds, drawMode, watchFaceColors)
        }
    ),

    STOLEN_FILLED(
        id = "style5_hands_id",
        nameResourceId = R.string.hands_style5_name,
        hourHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas = canvas,
                bounds = bounds,
                length = min(bounds.width(), bounds.height()) / 2 * 0.5f,
                angle = angle,
                fillMode = HandFillMode.SOLID
            )
        },
        minuteHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas = canvas,
                bounds = bounds,
                length = min(bounds.width(), bounds.height()) / 2 * 0.7f,
                angle = angle,
                fillMode = HandFillMode.SOLID
            )
        },
        secondHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * 0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.9f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * -0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * -0.1f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSecondsHandStandoff(canvas, bounds, drawMode, watchFaceColors)
        }
    ),

    FLOATING(
        id = "style6_hands_id",
        nameResourceId = R.string.hands_style6_name,
        hourHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas = canvas,
                bounds = bounds,
                length = min(bounds.width(), bounds.height()) / 2 * 0.5f,
                angle = angle,
                drawStandoff = false,
                fillMode = HandFillMode.DARKENED
            )
        },
        minuteHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas = canvas,
                bounds = bounds,
                length = min(bounds.width(), bounds.height()) / 2 * 0.7f,
                angle = angle,
                drawStandoff = false,
                fillMode = HandFillMode.DARKENED
            )
        },
        secondHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * 0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.9f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * -0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * -0.1f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSecondsHandStandoff(canvas, bounds, drawMode, watchFaceColors)
        }
    ),

    FLOATING_FILLED(
        id = "style7_hands_id",
        nameResourceId = R.string.hands_style7_name,
        hourHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas = canvas,
                bounds = bounds,
                length = min(bounds.width(), bounds.height()) / 2 * 0.5f,
                angle = angle,
                drawStandoff = false,
                fillMode = HandFillMode.SOLID
            )
        },
        minuteHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas = canvas,
                bounds = bounds,
                length = min(bounds.width(), bounds.height()) / 2 * 0.7f,
                angle = angle,
                drawStandoff = false,
                fillMode = HandFillMode.SOLID
            )
        },
        secondHandDrawFunction = { canvas, bounds, drawMode, watchFaceColors, angle ->
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * 0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * 0.9f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSimpleHand(
                canvas = canvas,
                bounds = bounds,
                centerSpacing = min(bounds.width(), bounds.height()) / 2 * -0.01f,
                length = min(bounds.width(), bounds.height()) / 2 * -0.1f,
                width = HAND_WIDTH_SECOND,
                color = 
                    if (drawMode == DrawMode.AMBIENT)
                        watchFaceColors.ambientTertiaryColor
                    else
                        watchFaceColors.activeTertiaryColor,
                angle = angle
            )
            drawSecondsHandStandoff(canvas, bounds, drawMode, watchFaceColors)
        }
    );

    companion object {
        /**
         * Helper method to generate previews on the fly
         */
        fun createPreviewBitmap(
            context: Context,
            style: HandsStyles
        ): Bitmap {
            val width = context.resources.displayMetrics.widthPixels
            val height = width

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val bounds = Rect(0, 0, width, height)

            // it would be nice to use the current style
            val watchFaceColors = WatchFaceColorPalette.convertToWatchFaceColorPalette(context, ColorStyle.STYLE1, ColorStyle.AMBIENT)

            style.hourHandDrawFunction?.invoke(canvas, bounds, DrawMode.INTERACTIVE, watchFaceColors, -60f)
            style.minuteHandDrawFunction?.invoke(canvas, bounds, DrawMode.INTERACTIVE, watchFaceColors, 60f)
            style.secondHandDrawFunction?.invoke(canvas, bounds, DrawMode.INTERACTIVE, watchFaceColors, 180f)

            return bitmap
        }

        /**
         * Translates the string id to the correct [HandsStyles] object.
         */
        fun getHandsStyleConfig(id: String): HandsStyles {
            return when (id) {
                MODERN.id -> MODERN
                BOLD.id -> BOLD
                BOLD_OUTLINE.id -> BOLD_OUTLINE
                STOLEN.id -> STOLEN
                STOLEN_FILLED.id -> STOLEN_FILLED
                FLOATING.id -> FLOATING
                FLOATING_FILLED.id -> FLOATING_FILLED

                else -> MODERN
            }
        }

        /**
         * Returns a list of [UserStyleSetting.ListUserStyleSetting.ListOption] for all
         * [BackgroundStyles] enums. The watch face settings APIs use this to set up
         * options for the user to select a style.
         */
        fun toOptionList(context: Context): List<ListUserStyleSetting.ListOption> {
            val handsStylesList = enumValues<HandsStyles>()

            return handsStylesList.map { style ->
                ListUserStyleSetting.ListOption(
                    UserStyleSetting.Option.Id(style.id),
                    context.resources,
                    style.nameResourceId,
                    style.nameResourceId,
                    Icon.createWithBitmap(createPreviewBitmap(context, style))
                )
            }
        }
    }
}
