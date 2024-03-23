package nodomain.pacjo.wear.watchface.editor.screens

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.editor.WatchFaceConfigStateHolder

@Composable
fun MiscConfigScreen(context: Context, stateHolder: WatchFaceConfigStateHolder, uiState: WatchFaceConfigStateHolder.UserStylesAndPreview) {
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
                PreferenceSwitch(
                    text = context.resources.getString(R.string.misc_complications_on_aod),
                    value = uiState.complicationsInAmbient,
                    onCheckedChange = { checked ->
                        stateHolder.setDrawComplicationsInAmbient(checked)
                    }
                )
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