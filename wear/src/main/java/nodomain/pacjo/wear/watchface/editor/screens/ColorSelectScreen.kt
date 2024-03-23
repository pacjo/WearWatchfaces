package nodomain.pacjo.wear.watchface.editor.screens

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds
import nodomain.pacjo.wear.watchface.editor.WatchFaceConfigStateHolder

@Composable
fun ColorSelectScreen(context: Context, stateHolder: WatchFaceConfigStateHolder, navController: NavController) {
    val listState = rememberScalingLazyListState()

    Scaffold (
        positionIndicator = {
            PositionIndicator(scalingLazyListState = listState)
        }
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState
        ) {
            item {
                Text(
                    text = context.resources.getString(R.string.colors_style_setting),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            items(ColorStyleIdAndResourceIds.toOptionList(context).size) { index ->
                val style = ColorStyleIdAndResourceIds.getColorStyleConfig(
                    ColorStyleIdAndResourceIds.toOptionList(context)[index].toString())

                if (style != ColorStyleIdAndResourceIds.AMBIENT) {
                    Button(
                        onClick = {
                            stateHolder.setColorStyle(
                                ColorStyleIdAndResourceIds.toOptionList(
                                    context
                                )[index].toString()
                            )
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(context.getColor(ColorStyleIdAndResourceIds.AMBIENT.outlineColorId))
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                            ) {
                                val colorList = listOf(
                                    Color(context.getColor(style.primaryColorId)),
                                    Color(context.getColor(style.secondaryColorId)),
                                    Color(context.getColor(style.tertiaryColorId))
                                )

                                var startAngle = -180f       // 0f is 3 o'clock
                                val sweepAngles = floatArrayOf(180f, 90f, 90f)
                                for (i in 0 until 3) {
                                    drawArc(
                                        color = colorList[i],
                                        startAngle = startAngle,
                                        sweepAngle = sweepAngles[i],
                                        useCenter = true,
                                    )
                                    startAngle += sweepAngles[i]
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = context.resources.getString(style.nameResourceId),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}