package nodomain.pacjo.wear.watchface.editor.screens

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_CORNER_RADIUS_MAXIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_CORNER_RADIUS_MINIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_CORNER_RADIUS_STEP
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_WIDTH_MAXIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_WIDTH_MINIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_WIDTH_STEP
import nodomain.pacjo.wear.watchface.editor.WatchFaceConfigStateHolder
import nodomain.pacjo.wear.watchface.editor.components.PreferenceSlider

@Composable
fun TimeRingSettingsScreen(
    context: Context,
    stateHolder: WatchFaceConfigStateHolder,
    uiState: WatchFaceConfigStateHolder.UserStylesAndPreview
) {
    val listState = rememberScalingLazyListState()

    Scaffold (
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        // might not be the best use of Lazy Column
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            item {
                Text(
                    text = context.resources.getString(R.string.time_ring_setting),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            // TODO: Add general toggle
            item {
                PreferenceSlider(
                    text = context.resources.getString(R.string.time_ring_width),
                    value = uiState.timeRingWidth,
                    steps = ((TIME_RING_WIDTH_MAXIMUM - TIME_RING_WIDTH_MINIMUM) / TIME_RING_WIDTH_STEP).toInt() - 1,
                    range = TIME_RING_WIDTH_MINIMUM..TIME_RING_WIDTH_MAXIMUM,
                    onValueChange = { value ->
                        stateHolder.setTimeRingWidth(value)
                    }
                )
            }
            item {
                PreferenceSlider(
                    text = context.resources.getString(R.string.time_ring_radius),
                    value = uiState.timeRingCornerRadius,
                    steps = ((TIME_RING_CORNER_RADIUS_MAXIMUM - TIME_RING_CORNER_RADIUS_MINIMUM) / TIME_RING_CORNER_RADIUS_STEP).toInt() - 1,
                    range = TIME_RING_CORNER_RADIUS_MINIMUM..TIME_RING_CORNER_RADIUS_MAXIMUM,
                    onValueChange = { value ->
                        stateHolder.setTimeRingCornerRadius(value)
                    }
                )
            }
        }
    }
}