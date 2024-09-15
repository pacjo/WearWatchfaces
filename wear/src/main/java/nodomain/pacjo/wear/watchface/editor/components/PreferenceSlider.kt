package nodomain.pacjo.wear.watchface.editor.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.Text

@Composable
fun PreferenceSlider(
    text: String,
    value: Float,
    steps: Int,
    range: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    val sliderValue = remember(value) { mutableFloatStateOf(value) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text
        )
        InlineSlider(
            value = sliderValue.floatValue,
            onValueChange = { newValue ->
                onValueChange(newValue)
                sliderValue.floatValue = newValue
            },
            steps = steps,
            decreaseIcon = { Icon(InlineSliderDefaults.Decrease, "Decrease") },
            increaseIcon = { Icon(InlineSliderDefaults.Increase, "Increase") },
            valueRange = range
        )
    }
}