package nodomain.pacjo.wear.watchface.base_digital

import androidx.wear.watchface.WatchFaceType
import nodomain.pacjo.wear.watchface.base.BaseWatchFaceService

abstract class DigitalWatchFaceService : BaseWatchFaceService() {
    final override val watchFaceType = WatchFaceType.DIGITAL
}