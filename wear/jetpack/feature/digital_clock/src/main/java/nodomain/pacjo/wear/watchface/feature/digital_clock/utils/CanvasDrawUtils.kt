package nodomain.pacjo.wear.watchface.feature.digital_clock.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.withClip

data class Alignment(
    val horizontal: Horizontal,
    val vertical: Vertical
) {
    companion object {
        enum class Horizontal {
            LEFT, CENTER, RIGHT
        }

        enum class Vertical {
            TOP, CENTER, BOTTOM
        }

        val CENTER = Alignment(Horizontal.CENTER, Vertical.CENTER)
    }
}

private fun calculateTextOffset(
    text: String,
    bounds: RectF,      // TODO: accept unit square bounds too
    paint: Paint,
    alignment: Alignment
): Pair<Float, Float> {
    val textBounds = Rect()
    val fontMetrics = paint.fontMetrics
    paint.getTextBounds(text, 0, text.length, textBounds)

    val x = when (alignment.horizontal) {
        Alignment.Companion.Horizontal.LEFT -> {
            paint.textAlign = Paint.Align.LEFT
            bounds.left
        }
        Alignment.Companion.Horizontal.CENTER -> {
            paint.textAlign = Paint.Align.CENTER
            bounds.centerX()
        }
        Alignment.Companion.Horizontal.RIGHT -> {
            paint.textAlign = Paint.Align.RIGHT
            bounds.right
        }
    }
    val y = when (alignment.vertical) {
        Alignment.Companion.Vertical.TOP -> {
            bounds.top - fontMetrics.ascent
        }
        Alignment.Companion.Vertical.CENTER -> {
            bounds.centerY() + textBounds.height() / 2f
        }
        Alignment.Companion.Vertical.BOTTOM -> {
            bounds.bottom - fontMetrics.descent
        }
    }

    return Pair(x, y)
}

// TODO: add multiline support
fun Canvas.drawTextInBounds(
    text: String,
    bounds: RectF,      // TODO: accept unit square bounds too
    paint: Paint,
    alignment: Alignment = Alignment.CENTER
) {
    val (x, y) = calculateTextOffset(text, bounds, paint, alignment)
    drawText(text, x, y, paint)
}

fun Canvas.drawTextFillBounds(
    text: String,
    bounds: RectF,      // TODO: accept unit square bounds too
    paint: Paint,
    alignment: Alignment = Alignment.CENTER
) {
    val textBounds = Rect()
    paint.getTextBounds(text, 0, text.length, textBounds)

    // math magic - scaling factor based on over/under-shoot
    val desiredTextSize = paint.textSize * minOf(bounds.width() / textBounds.width(), bounds.height() / textBounds.height())
    paint.textSize = desiredTextSize

    val (x, y) = calculateTextOffset(text, bounds, paint, alignment)
    drawText(text, x, y, paint)
}