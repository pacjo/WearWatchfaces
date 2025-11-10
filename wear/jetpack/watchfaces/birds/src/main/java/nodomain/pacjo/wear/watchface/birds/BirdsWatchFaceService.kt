package nodomain.pacjo.wear.watchface.birds

import android.graphics.Color
import android.graphics.RectF
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.data.ComplicationType
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.base_digital.DigitalWatchFaceService
import nodomain.pacjo.wear.watchface.feature.background.BackgroundFeature
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationSlotDefinition
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationsFeature
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.date
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.general
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.steps
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.textOnly
import nodomain.pacjo.wear.watchface.feature.background.backgrounds.StaticImageBackground
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory

class BirdsWatchFaceService : DigitalWatchFaceService() {
    override fun getFeatureFactories(): List<FeatureFactory> {
        return super.getFeatureFactories() + listOf(
            BackgroundFeature.Companion(
                listOf(
                    StaticImageBackground(this, R.drawable.bird_1),
                    StaticImageBackground(this, R.drawable.bird_2),
                    StaticImageBackground(this, R.drawable.bird_3),
                    StaticImageBackground(this, R.drawable.bird_4),
                    StaticImageBackground(this, R.drawable.bird_5),
                    StaticImageBackground(this, R.drawable.bird_6),
                    StaticImageBackground(this, R.drawable.bird_7)
                )
            ),
            ComplicationsFeature {
                val typeface = this.resources.getFont(R.font.spline_sans)
                val activeStyle = ComplicationSlotDefinition.defaultComplicationStyle.apply {
                    // correct some colors, since we have a white background
                    iconColor = Color.BLACK
                    textColor = Color.BLACK
                    titleColor = Color.DKGRAY

                    rangedValuePrimaryColor = Color.BLACK
                    rangedValueSecondaryColor = Color.DKGRAY

                    setTitleTypeface(typeface)
                    setTextTypeface(typeface)
                }

                listOf(
                    // top non-configurable date
                    ComplicationSlotDefinition(
                        id = 100,
                        bounds = RectF(0.35f, 0.10f, 0.65f, 0.20f),
                        supportedTypes = ComplicationType.textOnly(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.date(),
                        activeStyle = activeStyle
                    ),
                    // large user-configurable one under clock
                    ComplicationSlotDefinition(
                        id = 200,
                        bounds = RectF(0.12f, 0.67f, 0.40f, 0.85f),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.steps(),
                        activeStyle = activeStyle
                    )
                )
            }
        )
    }

    override fun createWatchFaceRenderer(): WatchFaceRenderer {
        return WatchFaceRendererImpl(this)
    }
}