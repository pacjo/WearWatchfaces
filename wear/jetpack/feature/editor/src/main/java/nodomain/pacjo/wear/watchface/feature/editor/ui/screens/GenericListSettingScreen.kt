package nodomain.pacjo.wear.watchface.feature.editor.ui.screens

import androidx.compose.runtime.Composable
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material3.TimeText
import androidx.wear.compose.material3.timeTextCurvedText
import androidx.wear.watchface.style.UserStyle
import androidx.wear.watchface.style.UserStyleSetting
import nodomain.pacjo.wear.watchface.feature.editor.ui.composables.ListOptionEntry

@Composable
fun GenericListSettingScreen(
    listSetting: UserStyleSetting.ListUserStyleSetting,
    userStyle: UserStyle,
    onOptionClick: (optionId: String) -> Unit
) {
    val listState = rememberScalingLazyListState()
    val currentOptionId = userStyle[listSetting]?.toString()

    Scaffold(
        vignette = {
            Vignette(vignettePosition = VignettePosition.TopAndBottom)
        },
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        },
        timeText = {
            TimeText {
                timeTextCurvedText(listSetting.displayName.toString())
            }
        }
    ) {
        ScalingLazyColumn(state = listState) {
            items(listSetting.options) { option ->
                ListOptionEntry(
                    option = option as UserStyleSetting.ListUserStyleSetting.ListOption,
                    isSelected = option.id.toString() == currentOptionId,
                    onClick = { onOptionClick(option.id.toString()) }
                )
            }
        }
    }
}