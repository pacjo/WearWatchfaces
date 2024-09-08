package nodomain.pacjo.wear.watchface.utils

import android.graphics.RectF
import android.os.Build
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.wear.watchface.ComplicationSlotBoundsType
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.SystemDataSources
import androidx.wear.watchface.complications.data.ComplicationType

const val TOP_HALF_WIDE_LEFT_COMPLICATION_ID = 100

const val TOP_QUARTER_WIDE_LEFT_COMPLICATION_ID = 110
const val TOP_QUARTER_WIDE_RIGHT_COMPLICATION_ID = 111

const val MIDDLE_WIDE_COMPLICATION_ID = 200

const val LEFT_OUTER_COMPLICATION_ID = 300
const val LEFT_INNER_COMPLICATION_ID = 301
const val RIGHT_OUTER_COMPLICATION_ID = 302
const val RIGHT_INNER_COMPLICATION_ID = 303

// Why `@Keep` you ask?
// It's always proguard (along with minify and shrink resources)

// Always breaking something
// Never telling what
// Errors impossible to find
// Cause me to suffer all day long

// type - one of ComplicationSlotBoundsType.(ROUND_RECT/EDGE/BACKGROUND)
@Keep
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
sealed class ComplicationConfig(
    val id: Int,
    val type: Int,
    val supportedTypes: List<ComplicationType>,
    val defaultDataSourcePolicy: DefaultComplicationDataSourcePolicy,
    val bounds: RectF
) {

    companion object {
        inline fun <reified T : ComplicationConfig> getAll(): List<T> {
            return T::class.nestedClasses.mapNotNull { nestedClass ->
                val instance = nestedClass.objectInstance as? T
                instance
            }
        }
    }

    @Keep
    data object TopHalfWideLeft: ComplicationConfig(
        TOP_HALF_WIDE_LEFT_COMPLICATION_ID,
        ComplicationSlotBoundsType.ROUND_RECT,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.RANGED_VALUE,
            ComplicationType.WEIGHTED_ELEMENTS,
            ComplicationType.LONG_TEXT
        ),
        DefaultComplicationDataSourcePolicy(
            SystemDataSources.DATA_SOURCE_SUNRISE_SUNSET,
            ComplicationType.SHORT_TEXT
        ),
        RectF(
            0.10f,
            0.02f,
            0.45f,
            0.15f
        )
    )

    @Keep
    data object TopQuarterWideLeft: ComplicationConfig(
        TOP_QUARTER_WIDE_LEFT_COMPLICATION_ID,
        ComplicationSlotBoundsType.ROUND_RECT,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.RANGED_VALUE,
            ComplicationType.WEIGHTED_ELEMENTS
        ),
        DefaultComplicationDataSourcePolicy(
            SystemDataSources.DATA_SOURCE_DATE,
            ComplicationType.SHORT_TEXT
        ),
        RectF(
            0.55f,
            0.02f,
            0.70f,
            0.15f
        )
    )

    @Keep
    data object TopQuarterWideRight: ComplicationConfig(
        TOP_QUARTER_WIDE_RIGHT_COMPLICATION_ID,
        ComplicationSlotBoundsType.ROUND_RECT,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.RANGED_VALUE,
            ComplicationType.WEIGHTED_ELEMENTS
        ),
        DefaultComplicationDataSourcePolicy(
            SystemDataSources.DATA_SOURCE_WATCH_BATTERY,
            ComplicationType.RANGED_VALUE
        ),
        RectF(
            0.75f,
            0.02f,
            0.90f,
            0.15f
        )
    )

    @Keep
    data object MiddleWide: ComplicationConfig(
        MIDDLE_WIDE_COMPLICATION_ID,
        ComplicationSlotBoundsType.ROUND_RECT,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.RANGED_VALUE,
            ComplicationType.WEIGHTED_ELEMENTS,
            ComplicationType.LONG_TEXT,
            ComplicationType.PHOTO_IMAGE
        ),
        DefaultComplicationDataSourcePolicy(
            SystemDataSources.DATA_SOURCE_WORLD_CLOCK,
            ComplicationType.LONG_TEXT
        ),
        RectF(
            0.10f,
            0.55f,
            0.90f,
            0.68f
        )
    )

    @Keep
    data object LeftOuter: ComplicationConfig(
        LEFT_OUTER_COMPLICATION_ID,
        ComplicationSlotBoundsType.ROUND_RECT,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.MONOCHROMATIC_IMAGE,
            ComplicationType.SMALL_IMAGE,
            ComplicationType.RANGED_VALUE,
            ComplicationType.GOAL_PROGRESS
        ),
        DefaultComplicationDataSourcePolicy(
            SystemDataSources.DATA_SOURCE_STEP_COUNT,
            ComplicationType.RANGED_VALUE
        ),
        RectF(
            0.06f,
            0.70f,
            0.24f,
            0.95f
        )
    )

    @Keep
    data object LeftInner: ComplicationConfig(
        LEFT_INNER_COMPLICATION_ID,
        ComplicationSlotBoundsType.ROUND_RECT,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.MONOCHROMATIC_IMAGE,
            ComplicationType.SMALL_IMAGE,
            ComplicationType.RANGED_VALUE,
            ComplicationType.GOAL_PROGRESS
        ),
        DefaultComplicationDataSourcePolicy(

        ),
        RectF(
            0.29f,
            0.70f,
            0.47f,
            0.95f
        )
    )

    @Keep
    data object RightInner: ComplicationConfig(
        RIGHT_INNER_COMPLICATION_ID,
        ComplicationSlotBoundsType.ROUND_RECT,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.MONOCHROMATIC_IMAGE,
            ComplicationType.SMALL_IMAGE,
            ComplicationType.RANGED_VALUE,
            ComplicationType.GOAL_PROGRESS
        ),
        DefaultComplicationDataSourcePolicy(

        ),
        RectF(
            0.52f,
            0.70f,
            0.70f,
            0.95f
        )
    )

    @Keep
    data object RightOuter: ComplicationConfig(
        RIGHT_OUTER_COMPLICATION_ID,
        ComplicationSlotBoundsType.ROUND_RECT,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.MONOCHROMATIC_IMAGE,
            ComplicationType.SMALL_IMAGE,
            ComplicationType.RANGED_VALUE,
            ComplicationType.GOAL_PROGRESS
        ),
        DefaultComplicationDataSourcePolicy(

        ),
        RectF(
            0.75f,
            0.70f,
            0.94f,
            0.95f
        )
    )
}