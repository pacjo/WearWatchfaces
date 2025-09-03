package nodomain.pacjo.wear.watchface.feature.editor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.watchface.style.UserStyle
import androidx.wear.watchface.style.UserStyleSetting
import nodomain.pacjo.wear.watchface.feature.editor.ui.composables.ListOptionEntry

@Composable
fun GenericListSettingScreen(
    listSetting: UserStyleSetting.ListUserStyleSetting,
    userStyle: UserStyle,
    onOptionClick: (optionId: String) -> Unit
) {
    val currentOptionId = userStyle[listSetting]?.toString()

    Column(
        modifier = Modifier.Companion
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = listSetting.displayName.toString(),
            style = MaterialTheme.typography.title3,
            textAlign = TextAlign.Companion.Center,
            modifier = Modifier.Companion.padding(bottom = 8.dp)
        )

        // A scrollable list for all the options of this setting
        ScalingLazyColumn(
            modifier = Modifier.Companion.fillMaxSize(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            items(listSetting.options) { option ->
                ListOptionEntry(
                    option = option,
                    isSelected = option.id.toString() == currentOptionId,
                    onClick = { onOptionClick(option.id.toString()) }
                )
            }
        }
    }
}