package nodomain.pacjo.wear.watchface.editor.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.AutoCenteringParams
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip

@Composable
fun MiscConfigScreen(settings: List<@Composable () -> Unit>) {
    val listState = rememberScalingLazyListState(0)

    Scaffold(
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            autoCentering = AutoCenteringParams(0, 1)
        ) {
            items(settings) { setting ->
                setting()
            }
        }
    }
}

@Composable
fun PreferenceSwitch(text: String, value: Boolean, onCheckedChange: (Boolean) -> Unit) {
    var checked by remember { mutableStateOf(value) }

    ToggleChip(
        checked = checked,
        onCheckedChange = {
            checked = !checked
            onCheckedChange(checked)
        },
        label = {
            Text(text)
        },
        toggleControl = {
            Switch(
                checked = checked
            )
        }
    )
}