package nodomain.pacjo.wear.watchface.feature.editor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.wear.watchface.client.ComplicationSlotState
import kotlin.collections.iterator

@Composable
fun ComplicationsSettingScreen(
    complicationSlotsStateMap: Map<Int, ComplicationSlotState>,
    onComplicationSlotClick: (Int) -> Unit
) {
    val density = LocalDensity.current

    Box(modifier = Modifier.fillMaxSize()) {
        for ((slotId, slotState) in complicationSlotsStateMap) {
            val bounds = slotState.bounds
            val left = with(density) { bounds.left.toDp() }
            val top = with(density) { bounds.top.toDp() }
            val right = with(density) { bounds.right.toDp() }
            val bottom = with(density) { bounds.bottom.toDp() }

            // create clickable area
            Box(
                modifier = Modifier
                    .offset(left, top)
                    .size((right - left), (bottom - top))
                    .clip(RoundedCornerShape(8.dp))
                    .clickable {
                        onComplicationSlotClick(slotId)
                    }
                    .background(Color.Red.copy(alpha = 0.25f))      // TODO: remove
            )
        }
    }
}