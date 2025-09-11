package nodomain.pacjo.wear.watchface.feature.colors.di

import android.graphics.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import nodomain.pacjo.wear.watchface.feature.colors.ColorAware
import nodomain.pacjo.wear.watchface.feature.colors.ColorStyle
import org.koin.dsl.module

val colorModule = module {
    /**
     * Dedicated CoroutineScope for color style management.
     */
    factory { CoroutineScope(Dispatchers.Main.immediate + SupervisorJob()) }

    /**
     * Creates a StateFlow that combines all registered ColorAware sources.
     */
    factory<StateFlow<ColorStyle>> {
        val coroutineScope: CoroutineScope = get()
        val sources: List<ColorAware> = getAll()
        val defaultStyle = ColorStyle(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK)

        if (sources.isEmpty()) {
            MutableStateFlow(defaultStyle)
        } else {
            combine(sources.map { it.colorStyleFlow }) { stylesArray ->
                stylesArray.firstNotNullOfOrNull { it } ?: defaultStyle
            }.stateIn(
                scope = coroutineScope,
                started = SharingStarted.Eagerly,
                initialValue = sources.first().colorStyleFlow.value
            )
        }
    }
}