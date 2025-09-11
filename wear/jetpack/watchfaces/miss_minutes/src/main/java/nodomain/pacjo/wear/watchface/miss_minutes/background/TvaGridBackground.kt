package nodomain.pacjo.wear.watchface.miss_minutes.background

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import androidx.core.graphics.toColorInt
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import nodomain.pacjo.wear.watchface.feature.rendering.utils.decodeAnimatedDrawable
import nodomain.pacjo.wear.watchface.feature.background.Background
import nodomain.pacjo.wear.watchface.feature.cell_grid.Grid2d
import nodomain.pacjo.wear.watchface.feature.cell_grid.GridSpec
import nodomain.pacjo.wear.watchface.feature.cell_grid.drawGridLines
import nodomain.pacjo.wear.watchface.miss_minutes.R
import java.time.ZonedDateTime

@RequiresApi(Build.VERSION_CODES.P)
class TvaGridBackground(
    val context: Context
) : Background() {
    override val id = "tva_grid"
    override val displayNameResourceId = R.string.tva_grid_background

    private val gridSize = 12
    private val grid = Grid2d(gridSize) { false }

    private val logoBitmap: Bitmap = context.getDrawable(R.drawable.tva_logo)!!.toBitmap()

    private val animationResources = listOf(
//        R.drawable.mm_blink,
        R.drawable.mm_dance,
        R.drawable.mm_pointing,
        R.drawable.mm_show,
        R.drawable.mm_sigh,
        R.drawable.mm_umbrela,
        R.drawable.mm_walk
    )
    private var animatedDrawable = decodeAnimatedDrawable(context, animationResources.random())

    private val drawablePaint = Paint().apply { isAntiAlias = true }

    private var lastZonedDateTime = ZonedDateTime.now()

    override fun draw(renderingContext: RenderingContext) {
        // change animation every 5 minutes
        if (renderingContext.zonedDateTime > lastZonedDateTime.plusMinutes(5)) {
            animatedDrawable = decodeAnimatedDrawable(context, animationResources.random())
            lastZonedDateTime = renderingContext.zonedDateTime
        }

        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            // fill background
            canvas.drawColor(Color.BLACK)

            // draw grid lines
            val gridSpec = GridSpec.fromBounds(bounds, gridSize)
            canvas.drawGridLines(grid, gridSpec, "#57280A".toColorInt(), 2f)

            // draw TVA logo
            // while we shouldn't probably scale this on every draw call,
            // bounds DO change and it's not that much work
            val logoScaleFactor = 0.15f         // TODO: scale properly regardless of screen size
            val scaledLogoBitmap = logoBitmap.scale(
                width = (logoBitmap.width * logoScaleFactor).toInt(),
                height = (logoBitmap.height * logoScaleFactor).toInt()
            )
            canvas.drawBitmap(scaledLogoBitmap, bounds.width() * 0.33f, bounds.height() * 0.80f, drawablePaint)

            // draw Miss Minutes animation
            val animationScaleFactor = 0.40f            // TODO: scale properly regardless of screen size
            val animationFrameBitmap = animatedDrawable.toBitmap()
            val scaledAnimationFrameBitmap = animationFrameBitmap.scale(
                width = (bounds.width() * animationScaleFactor).toInt(),
                height = (bounds.height() * animationScaleFactor).toInt()
            )
            canvas.drawBitmap(scaledAnimationFrameBitmap, bounds.width() * 0.30f, bounds.height() * 0.20f, drawablePaint)
        }
        // TODO: support opengl
    }
}