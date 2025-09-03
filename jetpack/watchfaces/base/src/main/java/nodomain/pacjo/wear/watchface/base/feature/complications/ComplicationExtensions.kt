package nodomain.pacjo.wear.watchface.base.feature.complications

import android.annotation.SuppressLint
import android.content.ComponentName
import android.graphics.RectF
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.SystemDataSources
import androidx.wear.watchface.complications.data.ComplicationType
import nodomain.pacjo.wear.watchface.base.utils.isAtLeastWearOs2
import nodomain.pacjo.wear.watchface.base.utils.isAtLeastWearOs3
import nodomain.pacjo.wear.watchface.base.utils.isAtLeastWearOs4

/**
 * Creates a list of common complication types for general-purpose slots.
 */
@SuppressLint("NewApi")     // unsupported types are ignored on older platforms
fun ComplicationType.Companion.general(): List<ComplicationType> = listOf(
    ComplicationType.SHORT_TEXT,
    ComplicationType.SMALL_IMAGE,
    ComplicationType.MONOCHROMATIC_IMAGE,
    ComplicationType.GOAL_PROGRESS,
    ComplicationType.WEIGHTED_ELEMENTS,
    ComplicationType.RANGED_VALUE,
    ComplicationType.LONG_TEXT,
)

/**
 * Creates a list of complication types suitable for small/corner slots.
 */
@SuppressLint("NewApi")     // unsupported types are ignored on older platforms
fun ComplicationType.Companion.compact(): List<ComplicationType> = listOf(
    ComplicationType.SHORT_TEXT,
    ComplicationType.RANGED_VALUE,
    ComplicationType.MONOCHROMATIC_IMAGE,
    ComplicationType.GOAL_PROGRESS,
    ComplicationType.WEIGHTED_ELEMENTS
)

/**
 * Creates a list of complication types for text-focused slots.
 */
fun ComplicationType.Companion.textOnly(): List<ComplicationType> = listOf(
    ComplicationType.SHORT_TEXT,
    ComplicationType.LONG_TEXT
)

/**
 * Creates a default complication data source policy with system provider fallbacks.
 */
fun DefaultComplicationDataSourcePolicy.Companion.empty(): DefaultComplicationDataSourcePolicy {
    return DefaultComplicationDataSourcePolicy()
}

/**
 * Creates a default complication data source policy for date complications.
 */
fun DefaultComplicationDataSourcePolicy.Companion.date(): DefaultComplicationDataSourcePolicy {
    return DefaultComplicationDataSourcePolicy(
        SystemDataSources.DATA_SOURCE_DAY_AND_DATE,
        ComplicationType.SHORT_TEXT
    )
}

/**
 * Creates a default complication data source policy for step count complications.
 */
fun DefaultComplicationDataSourcePolicy.Companion.steps(): DefaultComplicationDataSourcePolicy {
    val slotType = if (isAtLeastWearOs4())
        ComplicationType.GOAL_PROGRESS
    else
        ComplicationType.SHORT_TEXT

    return DefaultComplicationDataSourcePolicy(
        SystemDataSources.DATA_SOURCE_STEP_COUNT,
        slotType
    )
}

/**
 * Creates a default complication data source policy for battery complications.
 */
fun DefaultComplicationDataSourcePolicy.Companion.battery(): DefaultComplicationDataSourcePolicy {
    return DefaultComplicationDataSourcePolicy(
        SystemDataSources.DATA_SOURCE_WATCH_BATTERY,
        ComplicationType.RANGED_VALUE
    )
}

/**
 * Creates a default complication data source policy for media player complications.
 */
fun DefaultComplicationDataSourcePolicy.Companion.mediaPlayer(): DefaultComplicationDataSourcePolicy {
    val primaryDataSource = when {
        // TODO: add providers for wear os 5 and 6
        isAtLeastWearOs4() -> ComponentName(
            "com.google.android.wearable.media.sessions",
            "com.google.android.clockwork.media.complication.ComplicationProviderService"
        )
        isAtLeastWearOs3() -> TODO("MediaPlayer complication ComponentName for Wear OS 3 is unknown")
        isAtLeastWearOs2() -> ComponentName(
            "com.google.android.wearable.app",
            "com.google.android.clockwork.home.complications.providers.CurrentMediaProviderService"
        )
        else -> TODO("what are you?, wear os 1?!")
    }

    return DefaultComplicationDataSourcePolicy(
        SystemDataSources.DATA_SOURCE_WATCH_BATTERY,
        ComplicationType.RANGED_VALUE
    )
}

// TODO: create DefaultComplicationDataSourcePolicy.Companion.weather()


// TODO: add bounds square (taking: cx, cy, size) and taking: cx, cy, width, height

/**
 * Extension function to easily create a date complication.
 */
fun ComplicationSlotDefinition.Companion.date(
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
fun ComplicationSlotDefinition.Companion.battery(
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
fun ComplicationSlotDefinition.Companion.steps(
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

///**
// * Enum for side positions to make side complication creation more readable.
// */
//enum class SidePosition {
//    LEFT,
//    RIGHT,
//    TOP,
//    BOTTOM
//}