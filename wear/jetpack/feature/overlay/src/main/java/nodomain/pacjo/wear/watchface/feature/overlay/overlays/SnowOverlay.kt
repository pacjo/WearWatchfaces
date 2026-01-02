package nodomain.pacjo.wear.watchface.feature.overlay.overlays

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import nodomain.pacjo.wear.watchface.feature.overlay.Overlay
import nodomain.pacjo.wear.watchface.feature.overlay.R
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import kotlin.math.sin
import kotlin.random.Random

class SnowOverlay : Overlay() {
    override val id = "snow"
    override val displayNameResourceId = R.string.snow_overlay

    private val snowflakeCount = 100
    private val snowflakes = List(snowflakeCount) { Snowflake() }
    private val paint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var lastBounds = Rect()

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, _ ->
            // reset flakes on bounds change
            if (lastBounds != bounds) {
                snowflakes.forEach { it.reset(bounds, true) }
                lastBounds = bounds
            }

            snowflakes.forEach { snowflake ->
                snowflake.update(bounds)
                canvas.drawCircle(
                    snowflake.x,
                    snowflake.y,
                    snowflake.radius,
                    paint.apply { alpha = snowflake.alpha }
                )
            }
        }
        // TODO: support opengl
    }
}

private class Snowflake {
    var x = 0f
    var y = 0f
    var radius = 0f
    var speed = 0f
    var windSpeed = 0f
    var windOffset = 0f
    var alpha = 255

    fun reset(bounds: Rect, isInit: Boolean = false) {
        // start position
        x = Random.nextFloat() * bounds.width()
        // scatter vertically on the initial place, place just above top edge for rest
        y = if (isInit) Random.nextFloat() * bounds.height() else -radius * 2

        val depth = Random.nextFloat()

        radius = 1f + (depth * 4f)
        speed = 1f + (depth * 3f)
        alpha = (100 + (depth * 155)).toInt()

        windSpeed = 0.02f + (Random.nextFloat() * 0.04f)
        windOffset = Random.nextFloat() * 100f
    }

    fun update(bounds: Rect) {
        y += speed
        // left/right sway as sine wave
        x += (sin(y * 0.01f + windOffset) + 0.5f)

        // loop back if goes over the bottom edge
        if (y > bounds.height() + radius) {
            reset(bounds)
        }
    }
}