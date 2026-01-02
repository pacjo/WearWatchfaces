package nodomain.pacjo.wear.watchface.feature.overlay.overlays

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import androidx.core.graphics.ColorUtils
import nodomain.pacjo.wear.watchface.feature.overlay.Overlay
import nodomain.pacjo.wear.watchface.feature.overlay.R
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import nodomain.pacjo.wear.watchface.feature.rendering.utils.RotatingQueue
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// hand translated, modified version of https://codepen.io/whqet/pen/abooRX
// we're skipping all Firework trail stuff and using particles directly
class FireworksOverlay : Overlay() {
    override val id = "fireworks"
    override val displayNameResourceId = R.string.fireworks_overlay

    val particles = mutableListOf<Particle>()

    var bounds = Rect()
    var hue: Float = 120f

    inner class Particle(
        var x: Float,
        var y: Float
    ) {
        // trail effect - TODO: move to Vector2d<Float> when that exists
        private val coordinates = RotatingQueue<Pair<Float, Float>>(5)

        val angle = Random.nextFloat(0f, 2 * PI.toFloat())
        var speed = Random.nextFloat(1f, 10f)
        val friction = 0.95f
        val gravity = 1
        val hue = Random.nextFloat(this@FireworksOverlay.hue - 50f, this@FireworksOverlay.hue + 50f)
        val brightness = Random.nextFloat(0.4f, 0.8f)
        var alpha = Random.nextInt(255)
        val decay = Random.nextInt(4, 8)

        // returns true instance should be destroyed
        fun update(): Boolean {
            coordinates.add(Pair(x, y))

            speed *= friction
            x += cos(angle) * speed
            y += sin(angle) * speed + gravity
            alpha -= decay

            // return true when it'll be no longer visible
            return (alpha <= decay)
        }

        fun draw(canvas: Canvas) {
            val trailPath = Path().apply {
                val (startX, startY) = coordinates.deque.last()
                moveTo(startX, startY)
                lineTo(x, y)
            }
            val trailPaint = Paint().apply {
                // we're not antialiasing, difference isn't
                // very visible, and this way should be faster

                // won't draw open path without stroke style
                style = Paint.Style.STROKE
                strokeWidth = 3f

                color = ColorUtils.HSLToColor(floatArrayOf(hue, 1f, brightness))
                alpha = this@Particle.alpha
            }
            canvas.drawPath(trailPath, trailPaint)
        }
    }

    private fun updateBounds(newBounds: Rect) {
        if (bounds == newBounds)
            return      // skip updating if the same

        bounds = newBounds
        particles.clear()
    }

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, _ ->
            updateBounds(bounds)

            hue = Random.nextFloat(0f, 360f)

            // create particles from new firework
            if (Random.nextInt(100) > 90) {
                val fireworkParticles = createParticles(
                    Random.nextInt(bounds.left, bounds.right).toFloat(),
                    Random.nextInt(bounds.top, bounds.bottom).toFloat()
                )
                particles.addAll(fireworkParticles)
            }

            // update all and draw what's left
            val particlesToBeDestroyed = particles.filter { it.update() }
            particles.removeAll(particlesToBeDestroyed)
            particles.forEach { it.draw(canvas) }
        }
        // TODO: support opengl
    }

    private fun createParticles(x: Float, y: Float): List<Particle> {
        val particleCount = 30
        return List(particleCount) { Particle(x, y) }
    }
}

private fun Random.nextFloat(min: Float, max: Float): Float =
    this.nextFloat() * (max - min) + min