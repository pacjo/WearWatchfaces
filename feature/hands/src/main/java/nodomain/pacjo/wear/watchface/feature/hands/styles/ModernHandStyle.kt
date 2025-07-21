package nodomain.pacjo.wear.watchface.hands.styles

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import nodomain.pacjo.wear.watchface.feature.hands.R
import nodomain.pacjo.wear.watchface.hands.HandStyle
import java.time.ZonedDateTime

object ModernHandStyle : HandStyle {
    override val id: String = "modern"
    override val displayNameResourceId: Int = R.string.hands_style1_name

    private val hourPaint = Paint().apply { color = Color.CYAN; isAntiAlias = true }
    private val minutePaint = Paint().apply { color = Color.CYAN; isAntiAlias = true }
    private val secondPaint = Paint().apply { color = Color.YELLOW; isAntiAlias = true }

    override fun draw(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
        // ... (time calculation is the same)
        val seconds = zonedDateTime.second + zonedDateTime.nano / 1_000_000_000f
        val minutes = zonedDateTime.minute + seconds / 60f
        val hours = zonedDateTime.hour + minutes / 60f

        val centerX = bounds.exactCenterX()
        val centerY = bounds.exactCenterY()

        canvas.save()

        canvas.rotate(hours * 30f, centerX, centerY)
        canvas.drawRect(centerX - 6f, centerY - (centerX * 0.5f), centerX + 6f, centerY + 12f, hourPaint)

        canvas.rotate(minutes * 6f, centerX, centerY)
        canvas.drawRect(centerX - 4f, centerY - (centerX * 0.75f), centerX + 4f, centerY + 16f, minutePaint)


        canvas.rotate(seconds * 6f, centerX, centerY)
        canvas.drawRect(centerX - 2f, centerY - (centerX * 0.9f), centerX + 2f, centerY, secondPaint)

        canvas.restore()
    }

    override fun drawPreview(canvas: Canvas, bounds: Rect) {
        // TODO: implement
    }
}