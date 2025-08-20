package nodomain.pacjo.wear.watchface.base.feature

import android.content.Context
import androidx.wear.watchface.style.UserStyleSetting

/**
 * The fundamental contract for any feature that can be added to a watch face.
 * A feature is a self-contained unit that provides user-configurable settings.
 */
interface WatchFaceFeature {
    /**
     * Provides the list of UserStyleSettings that this feature manages.
     * This is used to build the UserStyleSchema.
     *
     * @param context context from which settings could be provided
     *
     * @return A list, as a single feature might control multiple settings (e.g., style and width).
     */
    fun getStyleSettings(context: Context): List<UserStyleSetting>
}