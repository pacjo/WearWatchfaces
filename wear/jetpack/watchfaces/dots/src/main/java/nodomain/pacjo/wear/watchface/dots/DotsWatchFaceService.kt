package nodomain.pacjo.wear.watchface.dots

import nodomain.pacjo.wear.watchface.base_analog.AnalogWatchFaceService
import nodomain.pacjo.wear.watchface.dots.background.DotsBackground
import nodomain.pacjo.wear.watchface.feature.background.BackgroundFeature
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.feature.hands.HandStyleFeature

class DotsWatchFaceService : AnalogWatchFaceService() {
    override fun getFeatureFactories(): List<FeatureFactory> {
        return listOf(
            HandStyleFeature(),
            BackgroundFeature.Companion(
                listOf(
                    DotsBackground
                )
            )
        )
    }
}