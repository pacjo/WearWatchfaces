package nodomain.pacjo.wear.watchface.data.watchface

import android.content.Context

/**
 * Color resources and drawable id needed to render the watch face. Translated from
 * [ColorStyleIdAndResourceIds] constant ids to actual resources with context at run time.
 *
 * This is only needed when the watch face is active.
 *
 * Note: We do not use the context to generate a [ComplicationDrawable] from the
 * complicationStyleDrawableId (representing the style), because a new, separate
 * [ComplicationDrawable] is needed for each complication. Because the renderer will loop through
 * all the complications and there can be more than one, this also allows the renderer to create
 * as many [ComplicationDrawable]s as needed.
 */
data class WatchFaceColorPalette(
    val activePrimaryColor: Int,
    val activeSecondaryColor: Int,
    val activeTertiaryColor: Int,
    val activeOutlineColor: Int,
    val ambientPrimaryColor: Int,
    val ambientSecondaryColor: Int,
    val ambientTertiaryColor: Int,
    val ambientOutlineColor: Int,
    val backgroundColor: Int
) {
    companion object {
        /**
         * Converts [ColorStyleIdAndResourceIds] to [WatchFaceColorPalette].
         */
        fun convertToWatchFaceColorPalette(
            context: Context,
            activeColorStyle: ColorStyleIdAndResourceIds,
            ambientColorStyle: ColorStyleIdAndResourceIds
        ): WatchFaceColorPalette {
            return WatchFaceColorPalette(
                // Active colors
                activePrimaryColor = context.getColor(activeColorStyle.primaryColorId),
                activeSecondaryColor = context.getColor(activeColorStyle.secondaryColorId),
                activeTertiaryColor = context.getColor(activeColorStyle.tertiaryColorId),
                activeOutlineColor = context.getColor(activeColorStyle.outlineColorId),
                // Ambient colors
                ambientPrimaryColor = context.getColor(ambientColorStyle.primaryColorId),
                ambientSecondaryColor = context.getColor(ambientColorStyle.secondaryColorId),
                ambientTertiaryColor = context.getColor(ambientColorStyle.tertiaryColorId),
                ambientOutlineColor = context.getColor(ambientColorStyle.outlineColorId),

                backgroundColor = context.getColor(ambientColorStyle.backgroundColorId)         // doesn't matter if it's active or ambient
            )
        }
    }
}
