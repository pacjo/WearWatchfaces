package nodomain.pacjo.wear.watchface.base_analog

import androidx.wear.watchface.WatchFaceType
import nodomain.pacjo.wear.watchface.base.BaseWatchFaceService

abstract class AnalogWatchFaceService : BaseWatchFaceService() {
    final override val watchFaceType = WatchFaceType.ANALOG
}