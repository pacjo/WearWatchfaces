package nodomain.pacjo.wear.watchface.base_digital

import androidx.wear.watchface.WatchFaceType
import nodomain.pacjo.wear.watchface.base.BaseWatchFaceService
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory

abstract class DigitalWatchFaceService : BaseWatchFaceService() {
    final override val watchFaceType = WatchFaceType.DIGITAL

    // provide some share digital features
    override fun getFeatureFactories(): List<FeatureFactory> {
        return emptyList()
    }
}