package nodomain.pacjo.wear.watchface.editor.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import nodomain.pacjo.wear.watchface.editor.WatchFaceConfigStateHolder
import nodomain.pacjo.wear.watchface.utils.ComplicationConfig
import nodomain.pacjo.wear.watchface.utils.DEFAULT_CORNER_RADIUS

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ComplicationConfigScreen(stateHolder: WatchFaceConfigStateHolder) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        for (complication in ComplicationConfig.getAll<ComplicationConfig>()) {
            val left = complication.bounds.left * screenWidth
            val top = complication.bounds.top * screenHeight
            val right = complication.bounds.right * screenWidth
            val bottom = complication.bounds.bottom * screenHeight

            // draw outline
            CanvasOutline(
                complication.bounds.left,
                complication.bounds.top,
                complication.bounds.right,
                complication.bounds.bottom
            )

            // create clickable area
            Box(
                modifier = Modifier
                    .offset(left, top)
                    .size((right - left), (bottom - top))
                    .clickable {
                        stateHolder.setComplication(complication.id)
                    }
            )
        }
    }
}

@Composable
fun CanvasOutline(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas { canvas ->
            val outlinePaint = Paint().apply {
                color = Color.DarkGray
                alpha = 100f
                isAntiAlias = true
                strokeWidth = 5f
            }

            canvas.drawRoundRect(
                left * size.width,
                top * size.height,
                right * size.width,
                bottom * size.height,
                DEFAULT_CORNER_RADIUS,
                DEFAULT_CORNER_RADIUS,
                outlinePaint
            )
        }
    }
}