package nodomain.pacjo.wear.watchface.nicely_blurry.background

import android.graphics.BlurMaskFilter
import android.graphics.Color
import android.graphics.Paint
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
import nodomain.pacjo.wear.watchface.feature.background.Background
import nodomain.pacjo.wear.watchface.nicely_blurry.R
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

object BlurryBlobsBackground : Background() {
    override val id = "blurry_blobs"
    override val displayNameResourceId = R.string.blurry_blobs_background

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            // fill background
            canvas.drawColor(Color.BLACK)

            val colors = listOf(Color.RED, Color.GREEN, Color.BLUE).reversed()     // TODO: make dynamic
            val numBlobs = colors.size
            val baseCycleTime = 10

            colors.forEachIndexed { i, blobColor ->
                val blobCycleTime = baseCycleTime * ((i + 1f) / numBlobs)
                val blobTimeOffset = (zonedDateTime.second % blobCycleTime + zonedDateTime.nano / 1_000_000_000f) / blobCycleTime

                val angle = 2 * PI * (blobTimeOffset + (i.toFloat() / numBlobs))
                val distance = min(bounds.width(), bounds.height()) * 0.16f

                val x = bounds.exactCenterX() + distance * cos(angle)
                val y = bounds.exactCenterY() + distance * sin(angle)

                val blobSize = distance * 1.25f     // looks about right

                val blobPaint = Paint().apply {
                    isAntiAlias = true
                    color = blobColor
                    alpha = 200
                    maskFilter = BlurMaskFilter(blobSize, BlurMaskFilter.Blur.NORMAL)
                }

                canvas.drawCircle(x.toFloat(), y.toFloat(), blobSize, blobPaint)
            }
        }
        // TODO: support opengl
    }

    override fun drawPreview(renderingContext: RenderingContext) {
        // arrange blobs nicely for preview
        val zonedDateTime = ZonedDateTime.now()
            .truncatedTo(ChronoUnit.MINUTES)

        super.drawPreview(renderingContext.copy(zonedDateTime = zonedDateTime))
    }
}