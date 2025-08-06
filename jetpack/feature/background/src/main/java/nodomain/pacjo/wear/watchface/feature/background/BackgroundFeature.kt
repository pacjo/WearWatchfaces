package nodomain.pacjo.wear.watchface.feature.background

import android.graphics.Canvas
import android.graphics.Rect
import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.feature.base.DrawableFeature
import nodomain.pacjo.wear.watchface.feature.base.ListFeature
import nodomain.pacjo.wear.watchface.feature.base.ListFeatureFactory
import java.time.ZonedDateTime

class BackgroundFeature(
    override val options: List<Background>
) : ListFeature<Background>(), DrawableFeature {
    override val featureId = FEATURE_ID
    override val featureDisplayNameResourceId = FEATURE_DISPLAY_NAME_RESOURCE_ID
    override val featureDescriptionResourceId = FEATURE_DESCRIPTION_RESOURCE_ID

    override val layer = WatchFaceLayer.BASE

    override fun draw(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime) {
        current.value.draw(canvas, bounds, zonedDateTime)
    }

    companion object {
        private const val FEATURE_ID: String = "background"
        private val FEATURE_DISPLAY_NAME_RESOURCE_ID: Int = R.string.background_setting
        private val FEATURE_DESCRIPTION_RESOURCE_ID: Int = R.string.background_setting_description
        private val OPTIONS = listOf<Background>()

        /**
         * The public constructor for this feature's factory.
         * Watch faces will call this to get a factory instance.
         * @param overrideOptions An optional list to use instead of the default.
         */
        operator fun invoke(overrideOptions: List<Background>? = null) = ListFeatureFactory(
            featureId = FEATURE_ID,
            featureDisplayNameResourceId = FEATURE_DISPLAY_NAME_RESOURCE_ID,
            featureDescriptionResourceId = FEATURE_DESCRIPTION_RESOURCE_ID,
            options = overrideOptions ?: OPTIONS,
            featureCreator = { scope, repo, options ->
                BackgroundFeature(options)
            }
        )
    }
}