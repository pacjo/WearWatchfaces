package nodomain.pacjo.wear.watchface.feature.base

import android.content.Context
import androidx.wear.watchface.style.CurrentUserStyleRepository
import kotlinx.coroutines.CoroutineScope

/**
 * A factory responsible for creating a single WatchFaceFeature.
 * It encapsulates the knowledge of how to instantiate a feature and its dependencies.
 */
interface FeatureFactory {
    /**
     * Creates an instance of a WatchFaceFeature.
     * @param context The service context.
     * @param coroutineScope The lifecycle scope of the watch face service.
     * @param currentUserStyleRepository The repository for managing user style selections.
     * @return A fully initialized WatchFaceFeature.
     */
    fun create(
        context: Context,
        coroutineScope: CoroutineScope,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFaceFeature
}