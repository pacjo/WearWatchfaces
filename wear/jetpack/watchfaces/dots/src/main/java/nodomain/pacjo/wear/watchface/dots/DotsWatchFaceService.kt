package nodomain.pacjo.wear.watchface.dots

import nodomain.pacjo.wear.watchface.base_analog.AnalogWatchFaceService
import nodomain.pacjo.wear.watchface.dots.background.DotsBackground
import nodomain.pacjo.wear.watchface.feature.background.BackgroundFeature
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory

class DotsWatchFaceService : AnalogWatchFaceService() {
    override fun getFeatureFactories(): List<FeatureFactory> {
        return super.getFeatureFactories() + listOf(
            BackgroundFeature.Companion(
                listOf(
                    DotsBackground
                )
            )
        )
    }
}