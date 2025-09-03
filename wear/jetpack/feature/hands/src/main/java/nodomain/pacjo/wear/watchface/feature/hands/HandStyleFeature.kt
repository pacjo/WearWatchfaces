package nodomain.pacjo.wear.watchface.feature.hands

import androidx.wear.watchface.style.WatchFaceLayer
import nodomain.pacjo.wear.watchface.base.feature.DrawableFeature
import nodomain.pacjo.wear.watchface.base.feature.ListFeature
import nodomain.pacjo.wear.watchface.base.feature.ListFeatureFactory
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext
import nodomain.pacjo.wear.watchface.feature.hands.styles.ClassicHandStyle
import nodomain.pacjo.wear.watchface.feature.hands.styles.ModernHandStyle

class HandStyleFeature(
    override val options: List<HandStyle>
) : ListFeature<HandStyle>(), DrawableFeature {
    override val featureId = FEATURE_ID
    override val featureDisplayNameResourceId = FEATURE_DISPLAY_NAME_RESOURCE_ID
    override val featureDescriptionResourceId = FEATURE_DESCRIPTION_RESOURCE_ID

    override val layer = WatchFaceLayer.COMPLICATIONS_OVERLAY

    override fun draw(renderingContext: RenderingContext) {
        current.value.draw(renderingContext)
    }

    companion object {
        private const val FEATURE_ID: String = "hand_style"
        private val FEATURE_DISPLAY_NAME_RESOURCE_ID: Int = R.string.hands_style_setting
        private val FEATURE_DESCRIPTION_RESOURCE_ID: Int = R.string.hands_style_setting_description
        private val OPTIONS = listOf(ClassicHandStyle, ModernHandStyle)

        /**
         * The public constructor for this feature's factory.
         * Watch faces will call this to get a factory instance.
         * @param overrideOptions An optional list to use instead of the default.
         */
        operator fun invoke(overrideOptions: List<HandStyle>? = null) = ListFeatureFactory(
            featureId = FEATURE_ID,
            featureDisplayNameResourceId = FEATURE_DISPLAY_NAME_RESOURCE_ID,
            featureDescriptionResourceId = FEATURE_DESCRIPTION_RESOURCE_ID,
            options = overrideOptions ?: OPTIONS,
            featureCreator = { scope, repo, options ->
                HandStyleFeature(options)
            }
        )
    }
}