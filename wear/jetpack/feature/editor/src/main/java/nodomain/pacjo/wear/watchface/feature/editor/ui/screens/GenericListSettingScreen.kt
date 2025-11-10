package nodomain.pacjo.wear.watchface.feature.editor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeTextDefaults

@Composable
fun GenericListSettingScreen(
    listSetting: UserStyleSetting.ListUserStyleSetting,
    userStyle: UserStyle,
    onOptionClick: (optionId: String) -> Unit
) {
    val isDisplayRound = LocalConfiguration.current.isScreenRound

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
            val settingName = listSetting.displayName.toString()

            if (isDisplayRound)
                TimeText {
                    timeTextCurvedText(settingName)
                }
            else
                // this is basically the material 3 TimeText but adapted for linear layout
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = settingName,
                        modifier = Modifier
                            .padding(TimeTextDefaults.ContentPadding)
                            .clip(RoundedCornerShape(100))
                            .background(TimeTextDefaults.backgroundColor())
                            .padding(horizontal = 8.dp, vertical = 1.dp),
                        color = TimeTextDefaults.timeTextStyle().color
                    )
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