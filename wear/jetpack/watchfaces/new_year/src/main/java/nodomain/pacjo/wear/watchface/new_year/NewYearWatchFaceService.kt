package nodomain.pacjo.wear.watchface.new_year

import android.graphics.Color
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.data.ComplicationType
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationSlotDefinition
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationsFeature
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.battery
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.general
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.steps
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.base_digital.DigitalWatchFaceService
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.feature.overlay.OverlayFeature
import nodomain.pacjo.wear.watchface.feature.overlay.overlays.FireworksOverlay
import nodomain.pacjo.wear.watchface.shared.GranularWatchFaceLayer
import nodomain.pacjo.wear.watchface.shared.utils.centeredRectF

class NewYearWatchFaceService : DigitalWatchFaceService() {

    override fun getFeatureFactories(): List<FeatureFactory> {
        return listOf(
            OverlayFeature.Companion(
                layer = GranularWatchFaceLayer.OVERLAY,
                overrideOptions = listOf(
                    FireworksOverlay()
                )
            ),
            ComplicationsFeature {
                val complicationRadius = 0.15f

                val style = ComplicationSlotDefinition.defaultComplicationStyle.apply {
                    iconColor = Color.LTGRAY
                    textColor = Color.LTGRAY
                    titleColor = Color.LTGRAY
                }

                listOf(
                    ComplicationSlotDefinition(
                        id = 100,
                        bounds = centeredRectF(0.25f, 0.8f, complicationRadius),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery(),
                        activeStyle = style,
                        ambientStyle = style
                    ),
                    ComplicationSlotDefinition(
                        id = 200,
                        bounds = centeredRectF(0.75f, 0.8f, complicationRadius),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.steps(),
                        activeStyle = style,
                        ambientStyle = style
                    )
                )
            }
        )
    }

    override fun createWatchFaceRenderer(): WatchFaceRenderer {
        return WatchFaceRendererImpl(this)
    }

}