package nodomain.pacjo.wear.watchface.editor.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.navigation.NavHostController
import nodomain.pacjo.wear.watchface.editor.WatchFaceConfigStateHolder
import nodomain.pacjo.wear.watchface.utils.ComplicationConfig

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ComplicationConfigScreen(
    stateHolder: WatchFaceConfigStateHolder
) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        for (complication in ComplicationConfig.getAll<ComplicationConfig>()) {
            val left = complication.bounds.left * screenWidth
            val top = complication.bounds.top * screenHeight
            val right = complication.bounds.right * screenWidth
            val bottom = complication.bounds.bottom * screenHeight

            // create clickable area
            Box(
                modifier = Modifier
                    .offset(left, top)
                    .size((right - left), (bottom - top))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        stateHolder.setComplication(complication.id)
                    }
            )
        }
    }
}