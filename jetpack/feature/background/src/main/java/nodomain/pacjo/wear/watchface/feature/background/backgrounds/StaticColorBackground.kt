package nodomain.pacjo.wear.watchface.feature.background.backgrounds

import android.graphics.Canvas
import android.graphics.Rect
import androidx.annotation.ColorInt
import nodomain.pacjo.wear.watchface.feature.background.R
import nodomain.pacjo.wear.watchface.feature.background.Background
import java.time.ZonedDateTime

class StaticColorBackground(
    @ColorInt val backgroundColor: Int
) : Background() {
    override val id: String = "static_color_$backgroundColor"
    override val displayNameResourceId: Int = R.string.static_color_background

    override fun draw(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
        canvas.drawColor(backgroundColor)
    }
}