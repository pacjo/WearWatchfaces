package nodomain.pacjo.wear.watchface.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.RenderParameters
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds
import nodomain.pacjo.wear.watchface.editor.WatchFaceConfigStateHolder
import java.time.ZonedDateTime

// constants
const val DEFAULT_CORNER_RADIUS = 60f

// Renderer
enum class AnimatedClockType {
    SECONDS,
    MINUTES,
    HOURS_12,
    HOURS_24
}

fun drawComplications(canvas: Canvas, zonedDateTime: ZonedDateTime, renderParameters: RenderParameters, complicationSlotsManager: ComplicationSlotsManager) {
    for ((_, complication) in complicationSlotsManager.complicationSlots) {
        if (complication.enabled) {
            complication.render(canvas, zonedDateTime, renderParameters)
        }
    }
}

// https://stackoverflow.com/a/24969713
fun drawTextCentredVertically(canvas: Canvas, paint: Paint, text: String, cx: Float, cy: Float) {
    val textBounds = Rect()

    paint.getTextBounds(text, 0, text.length, textBounds)
    // canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint) // center hor and ver
    canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint) // center ver only
}

fun drawTextCentredBoth(canvas: Canvas, paint: Paint, text: String, cx: Float, cy: Float) {
    val textBounds = Rect()

    paint.getTextBounds(text, 0, text.length, textBounds)
    // canvas.drawText(text, cx - textBounds.exactCenterX(), cy - textBounds.exactCenterY(), paint) // center hor and ver
    canvas.drawText(text, cx, cy - textBounds.exactCenterY(), paint.apply {
        textAlign = Paint.Align.CENTER
    })
}

fun drawScrollingFragment(
    canvas: Canvas,
    zonedDateTime: ZonedDateTime,
    paint: Paint,
    cx: Float,
    cy: Float,
    offset: Float,
    type: AnimatedClockType
) {
    // TODO: change or at least calculate properly
    val clip = RectF(
        cx - paint.textSize / 1.8f,
        cy - paint.textSize / 2.5f,
        cx + paint.textSize / 1.8f,
        cy + paint.textSize / 2.5f
    )

    // Apply clip
    canvas.save()
    canvas.clipRect(clip)

    for (i in -1..1) {
        val time = when (type) {
            AnimatedClockType.HOURS_12 -> TODO("implement (overall) 12 hour support")
            AnimatedClockType.HOURS_24 -> {
                // make sure we don't show 24
                if (zonedDateTime.hour + i <= 23)
                    (zonedDateTime.hour + i).toString().padStart(2, '0')
                else
                    "00"
            }
            AnimatedClockType.MINUTES -> {
                // make sure we don't show 60
                if (zonedDateTime.minute + i <= 59)
                    (zonedDateTime.minute + i).toString().padStart(2, '0')
                else
                    "00"
            }
            AnimatedClockType.SECONDS -> {
                // make sure we don't show 60
                if (zonedDateTime.second + i <= 59)
                    (zonedDateTime.second + i).toString().padStart(2, '0')
                else
                    "00"
            }
        }

        drawTextCentredBoth(
            canvas,
            paint,
            time,
            cx,
            cy - paint.textSize * (i - offset),
        )
    }

    canvas.restore()

    // top and bottom vignette
    val vignettePaint = Paint().apply {
        isAntiAlias = true
        shader = LinearGradient(
            0f, clip.top, 0f, clip.bottom,
            intArrayOf(
                android.graphics.Color.BLACK,
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.BLACK
            ),
            floatArrayOf(0f, 0.05f, 0.95f, 1f),
            Shader.TileMode.CLAMP
        )
    }

    canvas.drawRect(clip, vignettePaint)
}

// Composables
// TODO: finish and rename
@Composable
fun watchFacePreview(stateHolder: WatchFaceConfigStateHolder): WatchFaceConfigStateHolder.UserStylesAndPreview? {
    val uiState by stateHolder.uiState.collectAsState()

    return when (val state = uiState) {
        is WatchFaceConfigStateHolder.EditWatchFaceUiState.Success -> {
            state.userStylesAndPreview
        }
        else -> null
    }
}

@Composable
fun CategorySelectButton(context: Context, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .background(
                    Color(context.getColor(ColorStyleIdAndResourceIds.AMBIENT.outlineColorId)),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text)
        }
    }
}