package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import androidx.annotation.FontRes
import androidx.wear.watchface.ComplicationSlot
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.complications.rendering.CanvasComplicationDrawable
import androidx.wear.watchface.complications.rendering.ComplicationStyle
import java.time.ZonedDateTime

/**
 * Basic complication drawing function, additional code can be added to [customizeComplication]
 * to change complications' behavior
 *
 * @param canvas canvas of which complications should be drawn
 * @param zonedDateTime [ZonedDateTime] to render with (passed to [ComplicationSlot.render])
 * @param renderParameters current [RenderParameters] (passed to [ComplicationSlot.render])
 * @param complicationSlotsManager current [ComplicationSlotsManager] from which complication slots are taken
 * @param customizeComplication allows for customization of complications (optional)
 */
fun drawComplications(
    canvas: Canvas,
    zonedDateTime: ZonedDateTime,
    renderParameters: RenderParameters,
    complicationSlotsManager: ComplicationSlotsManager,
    customizeComplication: ((ComplicationSlot) -> Unit)? = null
) {
    for ((_, complicationSlot) in complicationSlotsManager.complicationSlots) {
        if (complicationSlot.enabled) {
            // apply additional styling if needed
            customizeComplication?.invoke(complicationSlot)

            // render
            complicationSlot.render(canvas, zonedDateTime, renderParameters)
        }
    }
}

/**
 * Hides border for complications in active and ambient states
 */
fun ComplicationSlot.hideBorders() {
    (renderer as CanvasComplicationDrawable).drawable.apply {
        activeStyle.borderStyle = ComplicationStyle.Companion.BORDER_STYLE_NONE
        ambientStyle.borderStyle = ComplicationStyle.Companion.BORDER_STYLE_NONE
    }
}

/**
 * Sets a custom typeface to be used for complications.
 * Meant to be used in conjunction with [Fonts]
 *
 * @param context context from which we can access font resource
 * @param fontResourceId resource id of the font to use (pass [null] to set default)
 */
fun ComplicationSlot.setTypeface(context: Context, @FontRes fontResourceId: Int?) {
    val typeface = fontResourceId?.let {
        context.resources.getFont(it)
    } ?: Typeface.create("sans-serif-condensed", Typeface.NORMAL)       // I'd love to use ComplicationStyle.TYPEFACE_DEFAULT instead, but google...

    (renderer as CanvasComplicationDrawable).drawable.apply {
        activeStyle.apply {
            setTitleTypeface(typeface)
            setTextTypeface(typeface)
        }
        ambientStyle.apply {
            setTitleTypeface(typeface)
            setTextTypeface(typeface)
        }
    }
}