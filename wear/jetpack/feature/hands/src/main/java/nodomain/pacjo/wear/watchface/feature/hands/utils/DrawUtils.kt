package nodomain.pacjo.wear.watchface.feature.hands.utils

import java.time.ZonedDateTime
import kotlin.Float

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