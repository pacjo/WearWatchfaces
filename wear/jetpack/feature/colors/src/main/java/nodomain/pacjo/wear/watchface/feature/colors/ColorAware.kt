package nodomain.pacjo.wear.watchface.feature.colors

import kotlinx.coroutines.flow.StateFlow

interface ColorAware {
    val colorStyleFlow: StateFlow<ColorStyle>
}