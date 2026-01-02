package nodomain.pacjo.wear.watchface.feature.background.backgrounds

import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import nodomain.pacjo.wear.watchface.feature.background.Background
import nodomain.pacjo.wear.watchface.feature.overlay.R
import nodomain.pacjo.wear.watchface.feature.rendering.utils.decodeAnimatedDrawable

@RequiresApi(Build.VERSION_CODES.P)
class GifPlayerBackground(
    context: Context,
    @DrawableRes backgroundDrawable: Int,
    @ColorInt val backgroundClearColor: Int = Color.BLACK
) : Background() {
    override val id = "gif_$backgroundDrawable"
    override val displayNameResourceId = R.string.gif_player_background

    private val animatedDrawable = decodeAnimatedDrawable(context, backgroundDrawable)

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, _ ->
            // clear canvas before drawing
            canvas.drawColor(backgroundClearColor)

            // I'd love to use AnimatedDrawable.draw(Canvas), but it doesn't respect set bounds
            // we extract the bitmap and scale it instead to work around the issue
            val bitmap = animatedDrawable.toBitmap()
            canvas.drawBitmap(bitmap, null, bounds, null)
        }
        // TODO: support opengl
    }
}