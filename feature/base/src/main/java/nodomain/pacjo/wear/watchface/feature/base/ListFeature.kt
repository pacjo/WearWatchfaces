package nodomain.pacjo.wear.watchface.feature.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.toIcon
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import androidx.core.graphics.createBitmap

/**
 * A contract for any option that can be displayed in a ListFeature.
 * It must provide its own ID, name, and a way to draw its preview icon.
 */
// TODO: extract to separate file?
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
 * A specialized WatchFaceFeature for presenting a list of options to the user.
 * It automatically handles the creation of the UserStyleSetting and its preview icons.
 */
interface ListFeature<T : FeatureOption> : WatchFaceFeature {
    val featureId: String
    @get:StringRes val featureDisplayNameResourceId: Int
    @get:StringRes val featureDescriptionResourceId: Int
    val options: List<T>

    override fun getStyleSettings(context: Context): List<UserStyleSetting> {
        // TODO: not sure about the preview code
        val settingOptions = options.map { option ->
            // 1. Create a bitmap for the preview
            val previewBitmap = createBitmap(100, 100)
            val canvas = Canvas(previewBitmap)
            val bounds = Rect(0, 0, 100, 100)

            // 2. Ask the option to draw its own preview
            option.drawPreview(canvas, bounds)

            // 3. Create the ListUserStyleSetting.Option
            ListUserStyleSetting.ListOption(
                UserStyleSetting.Option.Id(option.id),
                context.resources,
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
                listOf(WatchFaceLayer.BASE)
            )
        )
    }
}