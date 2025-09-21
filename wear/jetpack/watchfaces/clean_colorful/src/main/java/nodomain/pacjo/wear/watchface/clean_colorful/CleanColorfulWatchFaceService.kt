package nodomain.pacjo.wear.watchface.clean_colorful

import android.graphics.Color
import androidx.core.graphics.toColorInt
import nodomain.pacjo.wear.watchface.base_analog.AnalogWatchFaceService
import nodomain.pacjo.wear.watchface.feature.background.BackgroundFeature
import nodomain.pacjo.wear.watchface.feature.background.backgrounds.ColorAwareBackground
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.feature.colors.ColorStyle
import nodomain.pacjo.wear.watchface.feature.colors.ColorStyleFeature
import nodomain.pacjo.wear.watchface.feature.hands.HandStyleFeature
import nodomain.pacjo.wear.watchface.feature.hands.styles.MinimalHandStyle

class CleanColorfulWatchFaceService : AnalogWatchFaceService() {
    override fun getFeatureFactories(): List<FeatureFactory> {
        return listOf(
            ColorStyleFeature.Companion(
                listOf(
                    // orange
                    ColorStyle(
                        primary = Color.BLACK,
                        secondary = Color.BLACK,
                        tertiary = Color.BLACK,
                        background = "#F98A44".toColorInt()
                    ),
                    // green
                    ColorStyle(
                        primary = "#391C19".toColorInt(),
                        secondary = "#391C19".toColorInt(),
                        tertiary = "#391C19".toColorInt(),
                        background = "#94C671".toColorInt()
                    ),
                    // red
                    ColorStyle(
                        primary = Color.LTGRAY,
                        secondary = Color.LTGRAY,
                        tertiary = Color.LTGRAY,
                        background = "#D62727".toColorInt()
                    ),
                    // blue
                    ColorStyle(
                        primary = "#CBCE64".toColorInt(),
                        secondary = "#CBCE64".toColorInt(),
                        tertiary = "#CBCE64".toColorInt(),
                        background = "#1679C3".toColorInt()
                    ),
                    // metallic with orange hands - TODO: this should be graphite
                    ColorStyle(
                        primary = "#E6653D".toColorInt(),
                        secondary = "#E6653D".toColorInt(),
                        tertiary = "#E6653D".toColorInt(),
                        background = "#191718".toColorInt()
                    ),
                    // greyish-blue with white hands
                    ColorStyle(
                        primary = Color.WHITE,
                        secondary = Color.WHITE,
                        tertiary = Color.WHITE,
                        background = "#3F4054".toColorInt()
                    ),
                    // pink with pink hands
                    ColorStyle(
                        primary = "#A02865".toColorInt(),
                        secondary = "#A02865".toColorInt(),
                        tertiary = "#A02865".toColorInt(),
                        background = "#E0C5CA".toColorInt()
                    )
                )
            ),
            BackgroundFeature.Companion(
                listOf(
                    ColorAwareBackground()
                )
            ),
            HandStyleFeature.Companion(
                listOf(
                    MinimalHandStyle()
                )
            )
        )
    }
}