package nodomain.pacjo.wear.watchface.dots

import nodomain.pacjo.wear.watchface.base.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.base_analog.AnalogWatchFaceService
import nodomain.pacjo.wear.watchface.dots.background.DotsBackground
import nodomain.pacjo.wear.watchface.feature.background.BackgroundFeature
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.feature.hands.HandStyleFeature

class DotsWatchFaceService : AnalogWatchFaceService() {
    override fun getFeatureFactories(): List<FeatureFactory> {
        return super.getFeatureFactories() + listOf(
            BackgroundFeature(
              listOf(
                  DotsBackground
              )
            ),
            HandStyleFeature()
        )
    }

    override fun createWatchFaceRenderer(): WatchFaceRenderer {
        return WatchFaceRendererImpl()
    }
}