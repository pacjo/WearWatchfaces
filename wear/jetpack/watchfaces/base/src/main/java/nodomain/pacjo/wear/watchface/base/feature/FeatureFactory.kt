package nodomain.pacjo.wear.watchface.base.feature

import android.content.Context
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSetting
import kotlinx.coroutines.CoroutineScope

/**
 * A factory responsible for creating a single WatchFaceFeature.
 * It encapsulates the knowledge of how to instantiate a feature and its dependencies.
 */
interface FeatureFactory {
    /**
     * Called EARLY in the lifecycle to build the UserStyleSchema.
     * This method should be lightweight and only return the settings definitions.
     */
    fun getStyleSettings(context: Context): List<UserStyleSetting>

    /**
     * Creates an instance of a WatchFaceFeature.
     *
     * @param context service context.
     * @param coroutineScope lifecycle scope of the watch face service.
     * @param currentUserStyleRepository repository for managing user style selections.
     * @param watchState watchface state which can be observed
     *
     * @return fully initialized WatchFaceFeature.
     */
    fun create(
        context: Context,
        coroutineScope: CoroutineScope,
        currentUserStyleRepository: CurrentUserStyleRepository,
        watchState: WatchState
    ): WatchFaceFeature
}