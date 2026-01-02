package nodomain.pacjo.wear.watchface.miss_minutes

import android.graphics.Color
import android.graphics.RectF
import androidx.core.graphics.toColorInt
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.data.ComplicationType
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.base_digital.DigitalWatchFaceService
import nodomain.pacjo.wear.watchface.feature.background.BackgroundFeature
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationSlotDefinition
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationsFeature
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.battery
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.date
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.general
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.steps
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.textOnly
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.miss_minutes.background.TvaGridBackground

class MissMinutesWatchFaceService : DigitalWatchFaceService() {
    override fun getFeatureFactories(): List<FeatureFactory> {
        return listOf(
            BackgroundFeature.Companion(
                listOf(
                    TvaGridBackground(this)
                )
            ),
            ComplicationsFeature {
                val color = "#F5790C".toColorInt()
                val typeface = this.resources.getFont(R.font.anonymous_pro)
                val activeStyle = ComplicationSlotDefinition.defaultComplicationStyle.apply {
                    backgroundColor = Color.argb(150, 0, 0, 0)      // transparent black

                    iconColor = color
                    textColor = color

                    setTitleTypeface(typeface)
                    setTextTypeface(typeface)
                }

                listOf(
                    // top non-configurable date
                    ComplicationSlotDefinition(
                        id = 100,
                        bounds = RectF(0.35f, 0.05f, 0.65f, 0.15f),
                        supportedTypes = ComplicationType.textOnly(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.date(),
                        activeStyle = activeStyle
                    ),
                    // center row:
                    // size: 0.20fx0.20f
                    ComplicationSlotDefinition(
                        id = 200,
                        bounds = RectF(0.05f, 0.40f, 0.25f, 0.60f),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery(),
                        activeStyle = activeStyle
                    ),
                    ComplicationSlotDefinition(
                        id = 201,
                        bounds = RectF(0.75f, 0.40f, 0.95f, 0.60f),
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