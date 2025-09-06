package nodomain.pacjo.wear.watchface.base.feature.complications.extensions

import android.content.ComponentName
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.SystemDataSources
import androidx.wear.watchface.complications.data.ComplicationType
import nodomain.pacjo.wear.watchface.base.utils.isAtLeastWearOs2
import nodomain.pacjo.wear.watchface.base.utils.isAtLeastWearOs3
import nodomain.pacjo.wear.watchface.base.utils.isAtLeastWearOs4

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