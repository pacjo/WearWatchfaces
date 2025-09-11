package nodomain.pacjo.wear.watchface.base_analog

import androidx.wear.watchface.WatchFaceType
import nodomain.pacjo.wear.watchface.base.BaseWatchFaceService
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.feature.hands.HandStyleFeature

abstract class AnalogWatchFaceService : BaseWatchFaceService() {
    final override val watchFaceType = WatchFaceType.ANALOG

    // provide some shared analog features
    override fun getFeatureFactories(): List<FeatureFactory> {
        return listOf(
            HandStyleFeature()
        )
    }
}