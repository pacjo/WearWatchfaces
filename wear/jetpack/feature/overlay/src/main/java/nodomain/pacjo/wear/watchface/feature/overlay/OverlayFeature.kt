package nodomain.pacjo.wear.watchface.feature.overlay

import nodomain.pacjo.wear.watchface.feature.base.DrawableFeature
import nodomain.pacjo.wear.watchface.feature.base.ListFeature
import nodomain.pacjo.wear.watchface.feature.base.ListFeatureFactory
import nodomain.pacjo.wear.watchface.feature.rendering.GranularWatchFaceLayer
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext

open class OverlayFeature<T : Overlay>(
    override val layer: GranularWatchFaceLayer,
    override val options: List<T>
) : ListFeature<T>(), DrawableFeature {
    override val featureId = FEATURE_ID
    override val featureDisplayNameResourceId = FEATURE_DISPLAY_NAME_RESOURCE_ID
    override val featureDescriptionResourceId = FEATURE_DESCRIPTION_RESOURCE_ID

    override fun draw(renderingContext: RenderingContext) {
        current.value.draw(renderingContext)
    }

    companion object {
        private const val FEATURE_ID = "overlay"
        private val FEATURE_DISPLAY_NAME_RESOURCE_ID = R.string.overlay_setting
        private val FEATURE_DESCRIPTION_RESOURCE_ID = R.string.overlay_setting_description
        private val OPTIONS = listOf<Overlay>()

        /**
         * The public constructor for this feature's factory.
         * Watch faces will call this to get a factory instance.
         * @param overrideOptions An optional list to use instead of the default.
         */
        operator fun <T : Overlay> invoke(
            layer: GranularWatchFaceLayer,
            overrideOptions: List<T>? = null
        ) = ListFeatureFactory(
            featureId = FEATURE_ID,
            featureDisplayNameResourceId = FEATURE_DISPLAY_NAME_RESOURCE_ID,
            featureDescriptionResourceId = FEATURE_DESCRIPTION_RESOURCE_ID,
            options = overrideOptions ?: OPTIONS,
            featureCreator = { _, _, options ->
                OverlayFeature(layer, options)
            }
        )
    }
}
