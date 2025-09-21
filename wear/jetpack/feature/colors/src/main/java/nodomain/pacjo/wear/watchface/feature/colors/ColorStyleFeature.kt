package nodomain.pacjo.wear.watchface.feature.colors

import nodomain.pacjo.wear.watchface.feature.base.ListFeature
import nodomain.pacjo.wear.watchface.feature.base.ListFeatureFactory

class ColorStyleFeature(
    override val options: List<ColorStyle>
) : ListFeature<ColorStyle>(), ColorAware {
    override val featureId = FEATURE_ID
    override val featureDisplayNameResourceId = FEATURE_DISPLAY_NAME_RESOURCE_ID
    override val featureDescriptionResourceId = FEATURE_DESCRIPTION_RESOURCE_ID

    override val colorStyleFlow = current

    companion object {
        private const val FEATURE_ID = "color_style"
        private val FEATURE_DISPLAY_NAME_RESOURCE_ID = R.string.color_style_setting
        private val FEATURE_DESCRIPTION_RESOURCE_ID = R.string.color_style_setting_description
        private val OPTIONS = listOf<ColorStyle>()

        /**
         * The public constructor for this feature's factory.
         * Watch faces will call this to get a factory instance.
         * @param overrideOptions An optional list to use instead of the default.
         */
        operator fun invoke(overrideOptions: List<ColorStyle>? = null) = ListFeatureFactory(
            featureId = FEATURE_ID,
            featureDisplayNameResourceId = FEATURE_DISPLAY_NAME_RESOURCE_ID,
            featureDescriptionResourceId = FEATURE_DESCRIPTION_RESOURCE_ID,
            options = overrideOptions ?: OPTIONS,
            featureCreator = { scope, repo, options ->
                val feature = ColorStyleFeature(options)
                feature.initialize(scope, repo)
                feature
            },
        )
    }
}