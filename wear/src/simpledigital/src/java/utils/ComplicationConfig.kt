package nodomain.pacjo.wear.watchface.utils

import android.graphics.RectF
import android.os.Build
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.SystemDataSources
import androidx.wear.watchface.complications.data.ComplicationType

const val TOP_LEFT_COMPLICATION_ID = 100
const val TOP_RIGHT_COMPLICATION_ID = 101

const val MIDDLE_COMPLICATION_ID = 200

const val BOTTOM_COMPLICATION_ID = 300

// Why `@Keep` you ask?
// It's always proguard (along with minify and shrink resources)

// Always breaking something
// Never telling what
// Errors impossible to find
// Cause me to suffer all day long

@Keep
sealed class ComplicationConfig(val id: Int, val supportedTypes: List<ComplicationType>, val defaultDataSourcePolicy: DefaultComplicationDataSourcePolicy, val bounds: RectF) {

    companion object {
        inline fun <reified T : ComplicationConfig> getAll(): List<T> {
            return T::class.nestedClasses.mapNotNull { nestedClass ->
                val instance = nestedClass.objectInstance as? T
                instance
            }
        }
    }

    @Keep
    data object TopLeft: ComplicationConfig(
        TOP_LEFT_COMPLICATION_ID,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.MONOCHROMATIC_IMAGE,
            ComplicationType.SMALL_IMAGE,
            ComplicationType.RANGED_VALUE
        ),
        DefaultComplicationDataSourcePolicy(
                SystemDataSources.DATA_SOURCE_DATE,
            ComplicationType.SHORT_TEXT
        ),
        RectF(
            0.62f,
            0.07f,
            0.78f,
            0.31f
        )
    )

    @Keep
    data object TopRight: ComplicationConfig(
        TOP_RIGHT_COMPLICATION_ID,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.MONOCHROMATIC_IMAGE,
            ComplicationType.SMALL_IMAGE,
            ComplicationType.RANGED_VALUE
        ),
        DefaultComplicationDataSourcePolicy(
            SystemDataSources.DATA_SOURCE_WATCH_BATTERY,
            ComplicationType.RANGED_VALUE
        ),
        RectF(
            0.79f,
            0.07f,
            0.95f,
            0.31f
        )
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Keep
    data object Middle: ComplicationConfig(
        MIDDLE_COMPLICATION_ID,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.LONG_TEXT,
            ComplicationType.SMALL_IMAGE,
            ComplicationType.PHOTO_IMAGE,
            ComplicationType.GOAL_PROGRESS,
            ComplicationType.WEIGHTED_ELEMENTS
        ),
        DefaultComplicationDataSourcePolicy(
            SystemDataSources.DATA_SOURCE_STEP_COUNT,
            ComplicationType.SHORT_TEXT
        ),
        RectF(
            0.62f,
            0.38f,
            0.95f,
            0.62f
        )
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Keep
    data object Bottom: ComplicationConfig(
        BOTTOM_COMPLICATION_ID,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.LONG_TEXT,
            ComplicationType.SMALL_IMAGE,
            ComplicationType.PHOTO_IMAGE,
            ComplicationType.GOAL_PROGRESS,
            ComplicationType.WEIGHTED_ELEMENTS
        ),
        DefaultComplicationDataSourcePolicy(
            SystemDataSources.DATA_SOURCE_NEXT_EVENT,
            ComplicationType.LONG_TEXT
        ),
        RectF(
            0.62f,
            0.69f,
            0.95f,
            0.93f
        )
    )
}
