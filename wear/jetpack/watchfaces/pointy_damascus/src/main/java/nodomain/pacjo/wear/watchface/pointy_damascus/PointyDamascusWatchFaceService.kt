package nodomain.pacjo.wear.watchface.pointy_damascus

import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.base_digital.DigitalWatchFaceService
import nodomain.pacjo.wear.watchface.feature.background.BackgroundFeature
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.pointy_damascus.background.DamascusBackground

class PointyDamascusWatchFaceService : DigitalWatchFaceService() {
    override fun getFeatureFactories(): List<FeatureFactory> {
        return listOf(
            BackgroundFeature.Companion(
                listOf(
                    DamascusBackground()
                )
            )
        )
    }

    override fun createWatchFaceRenderer(): WatchFaceRenderer {
        return WatchFaceRendererImpl(this)
    }
}