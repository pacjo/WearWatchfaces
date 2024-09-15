package nodomain.pacjo.wear.watchface.editor.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip

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