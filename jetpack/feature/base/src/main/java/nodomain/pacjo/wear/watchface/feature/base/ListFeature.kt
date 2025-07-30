package nodomain.pacjo.wear.watchface.feature.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import androidx.annotation.StringRes
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toIcon
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import kotlin.math.min

/**
 * A contract for any option that can be displayed in a ListFeature.
 * It must provide its own ID, name, and a way to draw its preview icon.
 */
interface FeatureOption {
    val id: String
    @get:StringRes val displayNameResourceId: Int

    /**
     * Draws a preview of this option into the provided canvas.
     * This is the key to automatic preview generation.
     * @param canvas The canvas to draw on.
     * @param bounds The drawing bounds.
     */
    fun drawPreview(canvas: Canvas, bounds: Rect)
}

/**
 * A specialized [WatchFaceFeature] for presenting a list of options to the user.
 * Implementations only need to provide the abstract properties - no method overrides required.
 */
abstract class ListFeature<T : FeatureOption> : WatchFaceFeature {
    abstract val featureId: String
    @get:StringRes abstract val featureDisplayNameResourceId: Int
    @get:StringRes abstract val featureDescriptionResourceId: Int
    abstract val options: List<T>

    final override fun getStyleSettings(context: Context): List<UserStyleSetting> {
        return generateStyleSettings(
            context = context,
            featureId = featureId,
            featureDisplayNameResourceId = featureDisplayNameResourceId,
            featureDescriptionResourceId = featureDescriptionResourceId,
            options = options
        )
    }
}

/**
 * Abstract factory for ListFeature implementations.
 * Handles the creation of UserStyleSettings automatically based on the provided properties.
 * Implementations just need to provide the abstract properties - no method overrides required.
 */
abstract class ListFeatureFactory<T : FeatureOption> : FeatureFactory {
    abstract val featureId: String
    @get:StringRes abstract val featureDisplayNameResourceId: Int
    @get:StringRes abstract val featureDescriptionResourceId: Int
    abstract val options: List<T>

    final override fun getStyleSettings(context: Context): List<UserStyleSetting> {
        return generateStyleSettings(
            context = context,
            featureId = featureId,
            featureDisplayNameResourceId = featureDisplayNameResourceId,
            featureDescriptionResourceId = featureDescriptionResourceId,
            options = options
        )
    }
}

/**
 * Internal function to generate style settings for list-based features.
 * Used by both ListFeature and ListFeatureFactory to avoid duplication.
 */
private fun <T : FeatureOption> generateStyleSettings(
    context: Context,
    featureId: String,
    @StringRes featureDisplayNameResourceId: Int,
    @StringRes featureDescriptionResourceId: Int,
    options: List<T>
): List<UserStyleSetting> {
    val settingOptions = options.map { option ->
        // 1. Create a bitmap for the preview
        val displayMetrics = context.resources.displayMetrics
        val maxSize = 400       // enforced by OS
        val width = min(displayMetrics.widthPixels, maxSize)
        val height = min(displayMetrics.heightPixels, maxSize)

        val previewBitmap = createBitmap(width, height)
        val canvas = Canvas(previewBitmap)
        val bounds = Rect(0, 0, width, height)

        // 2. Ask the option to draw its own preview
        option.drawPreview(canvas, bounds)

        // 3. Create the ListUserStyleSetting.Option
        ListUserStyleSetting.ListOption(
            UserStyleSetting.Option.Id(option.id),
            context.resources,
            option.displayNameResourceId,
            option.displayNameResourceId,
            previewBitmap.toIcon()
        )
    }

    return listOf(
        ListUserStyleSetting(
            UserStyleSetting.Id(featureId),
            context.resources,
            featureDisplayNameResourceId,
            featureDescriptionResourceId,
            icon = null,
            options = settingOptions,
            WatchFaceLayer.ALL_WATCH_FACE_LAYERS        // TODO: make configurable
        )
    )
}