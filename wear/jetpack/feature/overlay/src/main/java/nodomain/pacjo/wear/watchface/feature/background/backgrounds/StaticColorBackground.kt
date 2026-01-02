package nodomain.pacjo.wear.watchface.feature.background.backgrounds

import androidx.annotation.ColorInt
import nodomain.pacjo.wear.watchface.shared.RenderingContext
import nodomain.pacjo.wear.watchface.feature.background.Background
import nodomain.pacjo.wear.watchface.feature.overlay.R

class StaticColorBackground(
    @ColorInt val backgroundColor: Int
) : Background() {
    override val id: String = "static_color_$backgroundColor"
    override val displayNameResourceId: Int = R.string.static_color_background

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, _, _ ->
            canvas.drawColor(backgroundColor)
        }
        // TODO: support opengl
    }
}