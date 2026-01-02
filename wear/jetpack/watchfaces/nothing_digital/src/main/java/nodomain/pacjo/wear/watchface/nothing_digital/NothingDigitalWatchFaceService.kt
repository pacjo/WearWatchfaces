package nodomain.pacjo.wear.watchface.nothing_digital

import android.graphics.Color
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.rendering.ComplicationStyle
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.base_digital.DigitalWatchFaceService
import nodomain.pacjo.wear.watchface.feature.background.BackgroundFeature
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationSlotDefinition
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationsFeature
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.battery
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.date
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.general
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.mediaPlayer
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.steps
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.textOnly
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.shared.utils.centeredRectF
import nodomain.pacjo.wear.watchface.nothing_digital.background.DotRingBackground

class NothingDigitalWatchFaceService : DigitalWatchFaceService() {
    override fun getFeatureFactories(): List<FeatureFactory> {
        return listOf(
            BackgroundFeature.Companion(
                listOf(
                    DotRingBackground
                )
            ),
            ComplicationsFeature {
                val activeStyle = ComplicationSlotDefinition.defaultComplicationStyle.apply {
                    backgroundColor = Color.TRANSPARENT
                    borderStyle = ComplicationStyle.BORDER_STYLE_NONE
                }

                val topBottomComplicationWidth = 0.3f
                val topBottomComplicationHeight = 0.1f

                val sideComplicationCenterY = 0.5f
                val sideComplicationDiameter = 0.2f

                listOf(
                    // top date
                    ComplicationSlotDefinition(
                        id = 100,
                        bounds = centeredRectF(0.5f, 0.15f, topBottomComplicationWidth, topBottomComplicationHeight),
                        supportedTypes = ComplicationType.textOnly(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.date(),
                        activeStyle = activeStyle
                    ),
                    // center row
                    ComplicationSlotDefinition(
                        id = 200,
                        bounds = centeredRectF(0.2f, sideComplicationCenterY, sideComplicationDiameter),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery(),
                        activeStyle = activeStyle
                    ),
                    ComplicationSlotDefinition(
                        id = 201,
                        bounds = centeredRectF(0.8f, sideComplicationCenterY, sideComplicationDiameter),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.mediaPlayer(),
                        activeStyle = activeStyle
                    ),
                    // bottom steps
                    ComplicationSlotDefinition(
                        id = 300,
                        bounds = centeredRectF(0.5f, 0.85f, topBottomComplicationWidth, topBottomComplicationHeight),
                        supportedTypes = ComplicationType.textOnly(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.steps(ComplicationType.SHORT_TEXT),
                        activeStyle = activeStyle
                    ),
                )
            }
        )
    }

    override fun createWatchFaceRenderer(): WatchFaceRenderer {
        return WatchFaceRendererImpl(this)
    }
}