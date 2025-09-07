package nodomain.pacjo.wear.watchface.nicely_blurry

import android.graphics.Color
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.SystemDataSources
import androidx.wear.watchface.complications.data.ComplicationType
import nodomain.pacjo.wear.watchface.base.feature.FeatureFactory
import nodomain.pacjo.wear.watchface.base.renderer.WatchFaceRenderer
import nodomain.pacjo.wear.watchface.base_digital.DigitalWatchFaceService
import nodomain.pacjo.wear.watchface.feature.background.BackgroundFeature
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationSlotDefinition
import nodomain.pacjo.wear.watchface.base.feature.complications.ComplicationsFeature
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.battery
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.copy
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.date
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.general
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.mediaPlayer
import nodomain.pacjo.wear.watchface.base.utils.centeredRectF
import nodomain.pacjo.wear.watchface.nicely_blurry.background.BlurryBlobsBackground

class NicelyBlurryWatchFaceService : DigitalWatchFaceService() {
    override fun getFeatureFactories(): List<FeatureFactory> {
        return super.getFeatureFactories() + listOf(
            BackgroundFeature.Companion(
                listOf(
                    BlurryBlobsBackground
                )
            ),
            ComplicationsFeature {
                val topRowCenterY = 0.42f
                val topRowWidth = 0.35f
                val topRowHeight = 0.12f
                val topRowComplications = listOf(
                    ComplicationSlotDefinition(
                        id = 100,
                        bounds = centeredRectF(0.30f, topRowCenterY, topRowWidth, topRowHeight),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy(
                            SystemDataSources.DATA_SOURCE_SUNRISE_SUNSET,
                            ComplicationType.SHORT_TEXT
                        )
                    ),
                    ComplicationSlotDefinition(
                        id = 101,
                        bounds = centeredRectF(0.70f, topRowCenterY, topRowWidth, topRowHeight),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.date()
                    )
                )

                val centerRowCenterY = 0.60f
                val centerRowSize = 0.17f
                val centerRowComplications = listOf(
                    ComplicationSlotDefinition(
                        id = 200,
                        bounds = centeredRectF(0.185f, centerRowCenterY, centerRowSize),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery()
                    ),
                    ComplicationSlotDefinition(
                        id = 201,
                        bounds = centeredRectF(0.395f, centerRowCenterY, centerRowSize),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery()
                    ),
                    ComplicationSlotDefinition(
                        id = 202,
                        bounds = centeredRectF(0.605f, centerRowCenterY, centerRowSize),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery()
                    ),
                    ComplicationSlotDefinition(
                        id = 203,
                        bounds = centeredRectF(0.815f, centerRowCenterY, centerRowSize),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery()
                    )
                )

                val bottomRowCenterY = 0.80f
                val bottomRowSize = 0.20f
                val bottomRowStyle = ComplicationSlotDefinition.defaultComplicationStyle
                    .copy()
                    .apply {
                        textColor = Color.BLACK
                        titleColor = Color.DKGRAY

                        iconColor = Color.BLACK

                        rangedValuePrimaryColor = Color.BLACK
                        rangedValueSecondaryColor = Color.DKGRAY
                    }
                val bottomRowComplications = listOf(
                    ComplicationSlotDefinition(
                        id = 300,
                        bounds = centeredRectF(0.28f, bottomRowCenterY, bottomRowSize),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery(),
                        activeStyle = bottomRowStyle,
                        ambientStyle = bottomRowStyle
                    ),
                    ComplicationSlotDefinition(
                        id = 301,
                        bounds = centeredRectF(0.50f, bottomRowCenterY, bottomRowSize),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.mediaPlayer(),
                        activeStyle = bottomRowStyle,
                        ambientStyle = bottomRowStyle
                    ),
                    ComplicationSlotDefinition(
                        id = 302,
                        bounds = centeredRectF(0.72f, bottomRowCenterY, bottomRowSize),
                        supportedTypes = ComplicationType.general(),
                        defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery(),
                        activeStyle = bottomRowStyle,
                        ambientStyle = bottomRowStyle
                    )
                )

                topRowComplications + centerRowComplications + bottomRowComplications
            }
        )
    }

    override fun createWatchFaceRenderer(): WatchFaceRenderer {
        return WatchFaceRendererImpl()
    }
}