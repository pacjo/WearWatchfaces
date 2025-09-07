package nodomain.pacjo.wear.watchface.feature.hands.utils

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.withRotation
import java.time.ZonedDateTime
import kotlin.Float
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class HandsStats(
    val hours: Float,
    val minutes: Float,
    val seconds: Float,

    val hoursHandAngle: Float,
    val minutesHandAngle: Float,
    val secondsHandAngle: Float
)

fun getHandsStats(zonedDateTime: ZonedDateTime): HandsStats {
    val seconds = zonedDateTime.second + zonedDateTime.nano / 1_000_000_000f
    val minutes = zonedDateTime.minute + seconds / 60f
    val hours = zonedDateTime.hour + minutes / 60f

    return HandsStats(
        hours = hours,
        minutes = minutes,
        seconds = seconds,
        hoursHandAngle = hours * 360f / 12f,
        minutesHandAngle = minutes * 360f / 60f,
        secondsHandAngle = seconds * 360f / 60f
    )
}

/**
 * Shortcut extension for drawing watch hands. Callback for each hand provides rotated
 * canvas environment and paint with angle-corrected shadow layer applied.
 */
fun Canvas.drawHands(
    bounds: Rect,
    zonedDateTime: ZonedDateTime,
    basePaint: Paint = Paint().apply { isAntiAlias = true },
    drawHourHand: (baseHourPaint: Paint) -> Unit,
    drawMinuteHand: (baseMinutePaint: Paint) -> Unit,
    drawSecondsHand: (baseSecondsPaint: Paint) -> Unit
) {
    val handsStats = getHandsStats(zonedDateTime)

    val centerX = bounds.exactCenterX()
    val centerY = bounds.exactCenterY()

    // hours
    withRotation(handsStats.hoursHandAngle, centerX, centerY) {
        val paint = Paint(basePaint).apply { applyShadow(handsStats.hoursHandAngle) }
        drawHourHand(paint)
    }

    // minutes
    withRotation(handsStats.minutesHandAngle, centerX, centerY) {
        val paint = Paint(basePaint).apply { applyShadow(handsStats.minutesHandAngle) }
        drawMinuteHand(paint)
    }

    // seconds
    withRotation(handsStats.secondsHandAngle, centerX, centerY) {
        val paint = Paint(basePaint).apply { applyShadow(handsStats.secondsHandAngle) }
        drawSecondsHand(paint)
    }
}

fun Paint.applyShadow(degrees: Float) {
    val shadowRadius = 2f
    val globalShadowDx = 2f
    val globalShadowDy = 2f

    val radians = ((degrees / 360f) * 2 * PI).toFloat()

    val localShadowDx = globalShadowDx * cos(radians) + globalShadowDy * sin(radians)
    val localShadowDy = -globalShadowDx * sin(radians) + globalShadowDy * cos(radians)

    this.setShadowLayer(shadowRadius, localShadowDx, localShadowDy, Color.DKGRAY)
}