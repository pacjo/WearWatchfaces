package nodomain.pacjo.wear.watchface.base.feature.complications

import android.graphics.RectF
import androidx.wear.watchface.complications.ComplicationSlotBounds
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.rendering.ComplicationStyle
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.battery
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.date
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.general
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

    // TODO: make better defaults
    /**
     * Default style when watchface is in active state.
     */
    val activeStyle: ComplicationStyle = ComplicationStyle(),

    /**
     * Default style when watchface is in ambient state.
     */
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
        fun date(
            id: Int,
            bounds: RectF,
            supportedTypes: List<ComplicationType> = ComplicationType.textOnly()
        ): ComplicationSlotDefinition {
            return ComplicationSlotDefinition(
                id = id,
                bounds = bounds,
                supportedTypes = supportedTypes,
                defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.date()
            )
        }

        fun battery(
            id: Int,
            bounds: RectF,
            supportedTypes: List<ComplicationType> = ComplicationType.general()
        ): ComplicationSlotDefinition {
            return ComplicationSlotDefinition(
                id = id,
                bounds = bounds,
                supportedTypes = supportedTypes,
                defaultDataSourcePolicy = DefaultComplicationDataSourcePolicy.battery()
            )
        }

        fun steps(
            id: Int,
            bounds: RectF,
            supportedTypes: List<ComplicationType> = ComplicationType.general()
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