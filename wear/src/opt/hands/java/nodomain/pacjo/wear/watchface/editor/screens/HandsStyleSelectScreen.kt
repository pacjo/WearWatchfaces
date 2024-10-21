package nodomain.pacjo.wear.watchface.editor.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyle
import nodomain.pacjo.wear.watchface.data.watchface.HandsStyles
import nodomain.pacjo.wear.watchface.editor.WatchFaceConfigStateHolder

@Composable
fun HandsStyleSelectScreen(context: Context, stateHolder: WatchFaceConfigStateHolder, navController: NavController) {
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
                    text = context.resources.getString(R.string.hands_style_setting),
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            items(HandsStyles.entries) { style ->
                Button(
                    onClick = {
                        stateHolder.setHandsStyle(style.id)
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(context.getColor(ColorStyle.AMBIENT.outlineColorId))
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = HandsStyles.createPreviewBitmap(context, style).asImageBitmap(),
                            contentDescription = stringResource(id = R.string.hands_style_preview_description),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(context.getColor(ColorStyle.AMBIENT.tertiaryColorId)))
                        )

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