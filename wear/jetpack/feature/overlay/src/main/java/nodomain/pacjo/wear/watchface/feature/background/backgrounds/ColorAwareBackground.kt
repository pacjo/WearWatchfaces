package nodomain.pacjo.wear.watchface.feature.background.backgrounds

import kotlinx.coroutines.flow.StateFlow
import nodomain.pacjo.wear.watchface.shared.RenderingContext
import nodomain.pacjo.wear.watchface.feature.background.Background
import nodomain.pacjo.wear.watchface.feature.colors.ColorStyle
import nodomain.pacjo.wear.watchface.feature.overlay.R
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

// not a object, since we want to setup Koin before this is created
class ColorAwareBackground : Background(), KoinComponent {
    override val id: String = "dynamic_color"
    override val displayNameResourceId: Int = R.string.color_aware_background

    private val colorStyleFlow: StateFlow<ColorStyle> by inject()

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, _, _ ->
            canvas.drawColor(colorStyleFlow.value.background)
        }
        // TODO: support opengl
    }
}