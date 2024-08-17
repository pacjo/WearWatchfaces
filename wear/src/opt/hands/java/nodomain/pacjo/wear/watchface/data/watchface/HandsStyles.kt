package nodomain.pacjo.wear.watchface.data.watchface

import android.content.Context
import android.graphics.*
import androidx.annotation.StringRes
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
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

const val STYLE1_HANDS_STYLE_ID = "style1_hands_id"
private val STYLE1_HANDS_STYLE_RESOURCE_ID = R.string.hands_style1_name

const val STYLE2_HANDS_STYLE_ID = "style2_hands_id"
private val STYLE2_HANDS_STYLE_RESOURCE_ID = R.string.hands_style2_name

const val STYLE3_HANDS_STYLE_ID = "style3_hands_id"
private val STYLE3_HANDS_STYLE_RESOURCE_ID = R.string.hands_style3_name

const val STYLE4_HANDS_STYLE_ID = "style4_hands_id"
private val STYLE4_HANDS_STYLE_RESOURCE_ID = R.string.hands_style4_name

const val STYLE5_HANDS_STYLE_ID = "style5_hands_id"
private val STYLE5_HANDS_STYLE_RESOURCE_ID = R.string.hands_style5_name

// TODO: change docs
/**
 * Represents watch face color style options the user can select (includes the unique id, the
 * complication style resource id, and general watch face color style resource ids).
 *
 * The companion object offers helper functions to translate a unique string id to the correct enum
 * and convert all the resource ids to their correct resources (with the Context passed in). The
 * renderer will use these resources to render the actual colors and ComplicationDrawables of the
 * watch face.
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
    isFilled: Boolean = false
) {
    val handPaint = createPaint(
        color,
        if (isFilled)
            Paint.Style.FILL
        else
            Paint.Style.STROKE,
        width
    )

    canvas.rotate(angle, bounds.centerX().toFloat(), bounds.centerY().toFloat())

    canvas.drawLine(
        bounds.centerX().toFloat(),
        bounds.centerY() - centerSpacing,
        bounds.centerX().toFloat(),
        bounds.centerY() - length,
        handPaint
    )

    handPaint.style = Paint.Style.FILL

    canvas.drawCircle(bounds.centerX().toFloat(), bounds.centerY() - centerSpacing, width / 2, handPaint)
    canvas.drawCircle(bounds.centerX().toFloat(), bounds.centerY() - length, width / 2, handPaint)

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
    renderParameters: RenderParameters,
    watchFaceColors: WatchFaceColorPalette
) {
    canvas.drawCircle(
        bounds.centerX().toFloat(),
        bounds.centerY().toFloat(),
        CIRCLE_RADIUS,
        createPaint(
            if (renderParameters.drawMode == DrawMode.AMBIENT)
                watchFaceColors.ambientTertiaryColor
            else
                watchFaceColors.activeTertiaryColor, Paint.Style.STROKE, HAND_WIDTH_SECOND
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
    val hourHandDrawFunction: ((Canvas, Rect, RenderParameters, WatchFaceColorPalette, Float) -> Unit)? = null,
    val minuteHandDrawFunction: ((Canvas, Rect, RenderParameters, WatchFaceColorPalette, Float) -> Unit)? = null,
    val secondHandDrawFunction: ((Canvas, Rect, RenderParameters, WatchFaceColorPalette, Float) -> Unit)? = null
) {
    MODERN(
        id = STYLE1_HANDS_STYLE_ID,
        nameResourceId = STYLE1_HANDS_STYLE_RESOURCE_ID,
        hourHandDrawFunction = { canvas, bounds, renderParameters, watchFaceColors, angle ->
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.04f,
                min(bounds.width(), bounds.height()) / 2 * 0.6f,
                HAND_WIDTH_SIMPLE,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientPrimaryColor
                else
                    watchFaceColors.activePrimaryColor,
                angle
            )
        },
        minuteHandDrawFunction = { canvas, bounds, renderParameters, watchFaceColors, angle ->
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.06f,
                min(bounds.width(), bounds.height()) / 2 * 0.7f,
                HAND_WIDTH_SIMPLE - 2f,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientSecondaryColor
                else
                    watchFaceColors.activeSecondaryColor,
                angle
            )
        },
        secondHandDrawFunction = { canvas, bounds, renderParameters, watchFaceColors, angle ->
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.01f,
                min(bounds.width(), bounds.height()) / 2 * 0.9f,
                HAND_WIDTH_SECOND,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientTertiaryColor
                else
                    watchFaceColors.activeTertiaryColor,
                angle
            )
            drawSecondsHandStandoff(canvas, bounds, renderParameters, watchFaceColors)
        }
    ),

    STOLEN(
        id = STYLE2_HANDS_STYLE_ID,
        nameResourceId = STYLE2_HANDS_STYLE_RESOURCE_ID,
        hourHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.5f,
                angle,
                fillMode = HandFillMode.DARKENED
            )
        },
        minuteHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.7f,
                angle,
                fillMode = HandFillMode.DARKENED
            )
        },
        secondHandDrawFunction = { canvas, bounds, renderParameters, watchFaceColors, angle ->
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.01f,
                min(bounds.width(), bounds.height()) / 2 * 0.9f,
                HAND_WIDTH_SECOND,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientTertiaryColor
                else
                    watchFaceColors.activeTertiaryColor,
                angle
            )
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * -0.01f,
                min(bounds.width(), bounds.height()) / 2 * -0.1f,
                HAND_WIDTH_SECOND,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientTertiaryColor
                else
                    watchFaceColors.activeTertiaryColor,
                angle
            )
            drawSecondsHandStandoff(canvas, bounds, renderParameters, watchFaceColors)
        }
    ),

    STOLEN_FILLED(
        id = STYLE3_HANDS_STYLE_ID,
        nameResourceId = STYLE3_HANDS_STYLE_RESOURCE_ID,
        hourHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.5f,
                angle,
                fillMode = HandFillMode.SOLID
            )
        },
        minuteHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.7f,
                angle,
                fillMode = HandFillMode.SOLID
            )
        },
        secondHandDrawFunction = { canvas, bounds, renderParameters, watchFaceColors, angle ->
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.01f,
                min(bounds.width(), bounds.height()) / 2 * 0.9f,
                HAND_WIDTH_SECOND,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientTertiaryColor
                else
                    watchFaceColors.activeTertiaryColor,
                angle
            )
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * -0.01f,
                min(bounds.width(), bounds.height()) / 2 * -0.1f,
                HAND_WIDTH_SECOND,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientTertiaryColor
                else
                    watchFaceColors.activeTertiaryColor,
                angle
            )
            drawSecondsHandStandoff(canvas, bounds, renderParameters, watchFaceColors)
        }
    ),

    FLOATING(
        id = STYLE4_HANDS_STYLE_ID,
        nameResourceId = STYLE4_HANDS_STYLE_RESOURCE_ID,
        hourHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.5f,
                angle,
                drawStandoff = false,
                fillMode = HandFillMode.DARKENED
            )
        },
        minuteHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.7f,
                angle,
                drawStandoff = false,
                fillMode = HandFillMode.DARKENED
            )
        },
        secondHandDrawFunction = { canvas, bounds, renderParameters, watchFaceColors, angle ->
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.01f,
                min(bounds.width(), bounds.height()) / 2 * 0.9f,
                HAND_WIDTH_SECOND,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientTertiaryColor
                else
                    watchFaceColors.activeTertiaryColor,
                angle
            )
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * -0.01f,
                min(bounds.width(), bounds.height()) / 2 * -0.1f,
                HAND_WIDTH_SECOND,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientTertiaryColor
                else
                    watchFaceColors.activeTertiaryColor,
                angle
            )
            drawSecondsHandStandoff(canvas, bounds, renderParameters, watchFaceColors)
        }
    ),

    FLOATING_FILLED(
        id = STYLE5_HANDS_STYLE_ID,
        nameResourceId = STYLE5_HANDS_STYLE_RESOURCE_ID,
        hourHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.5f,
                angle,
                drawStandoff = false,
                fillMode = HandFillMode.SOLID
            )
        },
        minuteHandDrawFunction = { canvas, bounds, _, _, angle ->
            drawStolenHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.7f,
                angle,
                drawStandoff = false,
                fillMode = HandFillMode.SOLID
            )
        },
        secondHandDrawFunction = { canvas, bounds, renderParameters, watchFaceColors, angle ->
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * 0.01f,
                min(bounds.width(), bounds.height()) / 2 * 0.9f,
                HAND_WIDTH_SECOND,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientTertiaryColor
                else
                    watchFaceColors.activeTertiaryColor,
                angle
            )
            drawSimpleHand(
                canvas,
                bounds,
                min(bounds.width(), bounds.height()) / 2 * -0.01f,
                min(bounds.width(), bounds.height()) / 2 * -0.1f,
                HAND_WIDTH_SECOND,
                if (renderParameters.drawMode == DrawMode.AMBIENT)
                    watchFaceColors.ambientTertiaryColor
                else
                    watchFaceColors.activeTertiaryColor,
                angle
            )
            drawSecondsHandStandoff(canvas, bounds, renderParameters, watchFaceColors)
        }
    );

    companion object {
        /**
         * Translates the string id to the correct object.
         */
        fun getHandsStyleConfig(id: String): HandsStyles {
            return when (id) {
                MODERN.id -> MODERN
                STOLEN.id -> STOLEN
                STOLEN_FILLED.id -> STOLEN_FILLED
                FLOATING.id -> FLOATING
                FLOATING_FILLED.id -> FLOATING_FILLED
                else -> MODERN
            }
        }

        /**
         * Returns a list of [UserStyleSetting.ListUserStyleSetting.ListOption] for all
         * HandsStyles enums. The watch face settings APIs use this to set up
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
                    null        // we don't use icons, TODO: we really should
                )
            }
        }
    }
}
