package nodomain.pacjo.wear.watchface.feature.hands

import android.content.Context
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nodomain.pacjo.wear.watchface.feature.base.ListFeature
import nodomain.pacjo.wear.watchface.feature.base.ListFeatureFactory
import nodomain.pacjo.wear.watchface.feature.base.WatchFaceFeature
import nodomain.pacjo.wear.watchface.feature.hands.styles.ClassicHandStyle
import nodomain.pacjo.wear.watchface.feature.hands.styles.ModernHandStyle

class HandStyleFeature(
    private val context: Context,
    scope: CoroutineScope,
    currentUserStyleRepository: CurrentUserStyleRepository
) : ListFeature<HandStyle>() {
    override val featureId = Companion.featureId
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
    companion object : ListFeatureFactory<HandStyle>() {
        override val featureId: String = "hand_style"
        override val featureDisplayNameResourceId: Int = R.string.hands_style_setting
        override val featureDescriptionResourceId: Int = R.string.hands_style_setting_description
        override val options = listOf(ClassicHandStyle, ModernHandStyle)

        override fun create(
            context: Context,
            coroutineScope: CoroutineScope,
            currentUserStyleRepository: CurrentUserStyleRepository
        ): WatchFaceFeature {
            return HandStyleFeature(context, coroutineScope, currentUserStyleRepository)
        }
    }
}