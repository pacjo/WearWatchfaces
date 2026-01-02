package nodomain.pacjo.wear.watchface.jimball

import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.base_digital.DigitalWatchFaceService
import nodomain.pacjo.wear.watchface.feature.background.BackgroundFeature
import nodomain.pacjo.wear.watchface.feature.background.backgrounds.GifPlayerBackground
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.jimball.sound.BackgroundMusicPlayerFeature

class JimballWatchFaceService : DigitalWatchFaceService() {
    override fun getFeatureFactories(): List<FeatureFactory> {
        return listOf(
            BackgroundFeature.Companion(
                listOf(
                    GifPlayerBackground(
                        context = this,
                        backgroundDrawable = R.drawable.jimball
                    )
                )
            ),
            BackgroundMusicPlayerFeature(R.raw.funkytown)
        )
    }

    override fun createWatchFaceRenderer(): WatchFaceRenderer {
        return WatchFaceRendererImpl(this)
    }
}