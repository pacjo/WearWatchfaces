package nodomain.pacjo.wear.watchface.utils

import android.graphics.RectF
import android.os.Build
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.wear.watchface.complications.DefaultComplicationDataSourcePolicy
import androidx.wear.watchface.complications.data.ComplicationType

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
}