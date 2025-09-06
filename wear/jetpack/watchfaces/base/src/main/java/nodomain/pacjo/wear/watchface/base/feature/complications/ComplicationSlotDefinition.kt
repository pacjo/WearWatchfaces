package nodomain.pacjo.wear.watchface.base.feature.complications

import android.graphics.RectF
import androidx.wear.watchface.complications.ComplicationSlotBounds
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.rendering.ComplicationStyle
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.battery
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.date
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.empty
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.steps
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.textOnly

/**
 * A single complication slot with all necessary configuration.
 */
data class ComplicationSlotDefinition(
    /**
     * Unique identifier for this complication slot.
     * Must be unique within a watch face.
     */
    val id: Int,

    /**
     * The bounds of the complication slot as a fraction of the watch face bounds.
     * Values should be between 0.0 and 1.0, where (0,0) is top-left and (1,1) is bottom-right.
     */
    val bounds: RectF,

    /**
     * List of complication types that this slot supports.
     * The first type in the list will be the default/preferred type.
     */
    val supportedTypes: List<ComplicationType>,

    /**
     * Default complication data source policy.
     * Defines what should be shown by default when no complication is configured.
     */
    val defaultDataSourcePolicy: DefaultComplicationDataSourcePolicy,

    // TODO: think about isEnabled and isVisible
    /**
     * Whether this complication slot is enabled by default.
     * Disabled slots won't be visible or interactive.
     */
    val isEnabled: Boolean = true,

    /**
     * Whether this complication slot is initially visible.
     * Can be used to hide/show complications based on user settings.
     */
    val isVisible: Boolean = true,

    // TODO: add docs
    // TODO: make better defaults
    val activeStyle: ComplicationStyle = ComplicationStyle(),
    val ambientStyle: ComplicationStyle = ComplicationStyle()
) {

    /**
     * Converts the fractional bounds to ComplicationSlotBounds.
     * The bounds are specified as fractions of the watch face size for flexibility.
     */
    fun toComplicationSlotBounds(): ComplicationSlotBounds {
        return ComplicationSlotBounds(bounds)
    }

    companion object {
        /**
         * Creates a circular complication slot centered at the given position.
         *
         * @param id Unique identifier for the slot
         * @param centerX X coordinate of center (0.0 to 1.0)
         * @param centerY Y coordinate of center (0.0 to 1.0)
         * @param width Width as fraction of watch face size
         * @param height Height as fraction of watch face size
         * @param supportedTypes List of supported complication types
         * @param defaultDataSourcePolicy Default data source
         */
        fun centered(
            id: Int,
            centerX: Float,
            centerY: Float,
            width: Float,
            height: Float,
            supportedTypes: List<ComplicationType>,
            defaultDataSourcePolicy: DefaultComplicationDataSourcePolicy = DefaultComplicationDataSourcePolicy.empty()
        ): ComplicationSlotDefinition {
            val bounds = RectF(
                centerX - width,
                centerY - height,
                centerX + width,
                centerY + height
            )

            return ComplicationSlotDefinition(
                id = id,
                bounds = bounds,
                supportedTypes = supportedTypes,
                defaultDataSourcePolicy = defaultDataSourcePolicy
            )
        }

        // TODO: add bounds square (taking: cx, cy, size) and taking: cx, cy, width, height

        /**
         * Extension function to easily create a date complication.
         */
        fun date(
            id: Int,
            bounds: RectF,
            supportedTypes: List<ComplicationType> = ComplicationType.textOnly() // TODO: check
        ): ComplicationSlotDefinition {
            return ComplicationSlotDefinition(
                id = id,
                bounds = bounds,
                supportedTypes = supportedTypes,
                defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.date()
            )
        }

        /**
         * Extension function to easily create a battery complication.
         */
        fun battery(
            id: Int,
            bounds: RectF,
            supportedTypes: List<ComplicationType> = listOf(ComplicationType.RANGED_VALUE, ComplicationType.SHORT_TEXT, ComplicationType.MONOCHROMATIC_IMAGE) // TODO: check
        ): ComplicationSlotDefinition {
            return ComplicationSlotDefinition(
                id = id,
                bounds = bounds,
                supportedTypes = supportedTypes,
                defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery()
            )
        }

        /**
         * Extension function to easily create a steps complication.
         */
        fun steps(
            id: Int,
            bounds: RectF,
            supportedTypes: List<ComplicationType> = listOf(ComplicationType.RANGED_VALUE, ComplicationType.SHORT_TEXT, ComplicationType.MONOCHROMATIC_IMAGE) // TODO: check
        ): ComplicationSlotDefinition {
            return ComplicationSlotDefinition(
                id = id,
                bounds = bounds,
                supportedTypes = supportedTypes,
                defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.steps()
            )
        }
    }
}