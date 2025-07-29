package nodomain.pacjo.wear.watchface.feature.hands

import android.content.Context
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.feature.base.ListFeature
import nodomain.pacjo.wear.watchface.feature.base.WatchFaceFeature
import nodomain.pacjo.wear.watchface.feature.hands.styles.ClassicHandStyle
import nodomain.pacjo.wear.watchface.feature.hands.styles.ModernHandStyle

class HandStyleFeature(
    private val context: Context,
    scope: CoroutineScope,
    currentUserStyleRepository: CurrentUserStyleRepository
) : ListFeature<HandStyle> {
    override val featureId = Companion.FEATURE_ID
    override val featureDisplayNameResourceId = Companion.featureDisplayNameResourceId
    override val featureDescriptionResourceId = Companion.featureDescriptionResourceId
    override val options = Companion.options

    // Public property to expose the currently selected style
    // TODO: check default code
    val currentHandStyle: StateFlow<HandStyle> =
        currentUserStyleRepository.userStyle.map { userStyle ->
            val styleId = userStyle[UserStyleSetting.Id(featureId)]?.toString() ?: options.first().id
            options.first { it.id == styleId }
        }.stateIn(
            scope,
            SharingStarted.Eagerly,
            options.first()
        )

    /**
     * The Factory is a companion object. It's a singleton that knows
     * how to create an instance of HandStyleFeature.
     */
    companion object : FeatureFactory {
        internal const val FEATURE_ID: String = "hand_style"
        internal val featureDisplayNameResourceId: Int = R.string.hands_style_setting
        internal val featureDescriptionResourceId: Int = R.string.hands_style_setting_description
        internal val options = listOf(ClassicHandStyle, ModernHandStyle)

        override fun getStyleSettings(context: Context): List<UserStyleSetting> {
            // This logic now correctly uses the single source of truth `options` list.
            val settingOptions = options.map { option ->
                UserStyleSetting.ListUserStyleSetting.ListOption(
                    UserStyleSetting.Option.Id(option.id),
                    context.resources,
                    option.displayNameResourceId,
                    option.displayNameResourceId,
                    icon = null     // TODO: change
                )
            }

            return listOf(
                UserStyleSetting.ListUserStyleSetting(
                    id = UserStyleSetting.Id(FEATURE_ID),
                    resources = context.resources,
                    featureDisplayNameResourceId,
                    featureDescriptionResourceId,
                    icon = null,
                    options = settingOptions,
                    affectsWatchFaceLayers = WatchFaceLayer.ALL_WATCH_FACE_LAYERS
                )
            )
        }


        override fun create(
            context: Context,
            coroutineScope: CoroutineScope,
            currentUserStyleRepository: CurrentUserStyleRepository
        ): WatchFaceFeature {
            return HandStyleFeature(context, coroutineScope, currentUserStyleRepository)
        }
    }
}