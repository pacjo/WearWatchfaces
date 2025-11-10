package nodomain.pacjo.wear.watchface.feature.background.backgrounds

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import nodomain.pacjo.wear.watchface.feature.background.Background
import nodomain.pacjo.wear.watchface.feature.background.R

class StaticImageBackground(
    context: Context,
    @DrawableRes backgroundDrawable: Int,
    @ColorInt val backgroundClearColor: Int = Color.BLACK
) : Background() {
    override val id = "image_$backgroundDrawable"
    override val displayNameResourceId = R.string.static_image_background

    private val imageBitmap = context.getDrawable(backgroundDrawable)!!.toBitmap()

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, _ ->
            // clear canvas before drawing
            canvas.drawColor(backgroundClearColor)

            canvas.drawBitmap(imageBitmap, null, bounds, null)
        }
        // TODO: support opengl
    }
}