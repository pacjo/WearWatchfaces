package nodomain.pacjo.wear.watchface.utils

import android.graphics.RectF
import android.os.Build
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.SystemDataSources
import androidx.wear.watchface.complications.data.ComplicationType

const val RIGHT_COMPLICATION_ID = 100

// Why `@Keep` you ask?
// It's always proguard (along with minify and shrink resources)

// Always breaking something
// Never telling what
// Errors impossible to find
// Cause me to suffer all day long

@Keep
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
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
    data object Right: ComplicationConfig(
        RIGHT_COMPLICATION_ID,
        listOf(
            ComplicationType.SHORT_TEXT,
            ComplicationType.MONOCHROMATIC_IMAGE,
            ComplicationType.SMALL_IMAGE,
            ComplicationType.RANGED_VALUE,
            ComplicationType.GOAL_PROGRESS
        ),
        DefaultComplicationDataSourcePolicy(
            SystemDataSources.DATA_SOURCE_DATE,
            ComplicationType.SHORT_TEXT
        ),
        RectF(
            0.77f,
            0.45f,
            0.94f,
            0.55f
        )
    )
}