package nodomain.pacjo.wear.watchface.editor

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.InlineSlider
import androidx.wear.compose.material.InlineSliderDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Switch
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ToggleChip
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.CORNER_RADIUS_DEFAULT
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds.Companion.getColorStyleConfig
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_CORNER_RADIUS_MAXIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_CORNER_RADIUS_MINIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_CORNER_RADIUS_STEP
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_WIDTH_MAXIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_WIDTH_MINIMUM
import nodomain.pacjo.wear.watchface.data.watchface.TIME_RING_WIDTH_STEP
import nodomain.pacjo.wear.watchface.utils.ComplicationConfig

class WatchFaceConfigActivity : ComponentActivity() {
    private lateinit var stateHolder: WatchFaceConfigStateHolder

    override fun onStart() {
        super.onStart()
        stateHolder = WatchFaceConfigStateHolder(
            lifecycleScope,
            this
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val uiState = watchFacePreview(stateHolder)
            val bitmap = uiState?.previewImage?.asImageBitmap()

            val context = LocalContext.current

            MaterialTheme {
                // background
                if (bitmap != null) {
                    Image(bitmap = bitmap, contentDescription = null)
                }

                // settings pages
                val horizontalPagerState = rememberPagerState { 4 }
                val pageIndicatorState: PageIndicatorState = remember {
                    object : PageIndicatorState {
                        override val pageOffset: Float
                            get() = 0F
                        override val selectedPage: Int
                            get() = horizontalPagerState.currentPage
                        override val pageCount: Int
                            get() = horizontalPagerState.pageCount
                    }
                }

                Scaffold(
                    positionIndicator = {
                        HorizontalPageIndicator(
                            modifier = Modifier
                                .padding(bottom = 4.dp),
                            pageIndicatorState = pageIndicatorState
                        )
                    }
                ) {
                    HorizontalPager(
                        modifier = Modifier.fillMaxSize(),
                        state = horizontalPagerState
                    ) { currentPage ->
                        when (currentPage) {
                            0 -> ColorSelectScreen(context, stateHolder)
                            1 -> TimeRingSettingsScreen(context, stateHolder, uiState!!)
                            2 -> ComplicationConfigScreen(stateHolder)
                            3 -> MiscConfigScreen(context, stateHolder, uiState!!)
                        }
                        // new ui, WIP
//                        when (currentPage) {
//                            0 -> CategorySelectButton(context, context.resources.getString(R.string.colors_style_setting), {  })
//                            1 -> CategorySelectButton(context, context.resources.getString(R.string.time_ring_setting), {  })
//                            2 -> CategorySelectButton(context, context.resources.getString(R.string.complications_setting), {  })
//                            3 -> MiscConfigScreen(stateHolder, uiState!!)
//                        }
                    }
                }
            }
        }
    }
}

// TODO: finish
@Composable
fun watchFacePreview(stateHolder: WatchFaceConfigStateHolder): WatchFaceConfigStateHolder.UserStylesAndPreview? {
    val uiState by stateHolder.uiState.collectAsState()

    return when (val state = uiState) {
        is WatchFaceConfigStateHolder.EditWatchFaceUiState.Success -> {
            state.userStylesAndPreview
        }
        else -> null
    }
}

@Composable
fun CategorySelectButton(context: Context, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .background(
                    Color(context.getColor(ColorStyleIdAndResourceIds.AMBIENT.outlineColorId)),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text)
        }
    }
}

@Composable
fun ColorSelectScreen(context: Context, stateHolder: WatchFaceConfigStateHolder) {
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
                val style = getColorStyleConfig(ColorStyleIdAndResourceIds.toOptionList(context)[index].toString())

                if (style != ColorStyleIdAndResourceIds.AMBIENT) {
                    Button(
                        onClick = {
                            stateHolder.setColorStyle(
                                ColorStyleIdAndResourceIds.toOptionList(
                                    context
                                )[index].toString()
                            )
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

@Composable
fun TimeRingSettingsScreen(context: Context, stateHolder: WatchFaceConfigStateHolder, uiState: WatchFaceConfigStateHolder.UserStylesAndPreview) {
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

@Composable
fun PreferenceSlider(text: String, value: Float, steps: Int, range: ClosedFloatingPointRange<Float>, onValueChange: (Float) -> Unit) {
    Column (
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text
        )
        InlineSlider(
            value = value,
            onValueChange = onValueChange,
            steps = steps,
            decreaseIcon = { Icon(InlineSliderDefaults.Decrease, "Decrease") },
            increaseIcon = { Icon(InlineSliderDefaults.Increase, "Increase") },
            valueRange = range
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ComplicationConfigScreen(stateHolder: WatchFaceConfigStateHolder) {

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    Box(modifier = Modifier.fillMaxSize()) {
        for (complication in ComplicationConfig.getAll<ComplicationConfig>()) {
            val left = complication.bounds.left * screenWidth
            val top = complication.bounds.top * screenHeight
            val right = complication.bounds.right * screenWidth
            val bottom = complication.bounds.bottom * screenHeight

            // draw outline
            CanvasOutline(
                complication.bounds.left,
                complication.bounds.top,
                complication.bounds.right,
                complication.bounds.bottom
            )

            // create clickable area
            Box(
                modifier = Modifier
                    .offset(left, top)
                    .size((right - left), (bottom - top))
                    .clickable {
                        stateHolder.setComplication(complication.id)
                    }
            )
        }
    }
}

@Composable
fun CanvasOutline(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas { canvas ->
            val outlinePaint = Paint().apply {
                color = Color.Red
                alpha = 100f
                isAntiAlias = true
                strokeWidth = 5f
            }

            canvas.drawRoundRect(
                left * size.width,
                top * size.height,
                right * size.width,
                bottom * size.height,
                CORNER_RADIUS_DEFAULT,
                CORNER_RADIUS_DEFAULT,
                outlinePaint
            )
        }
    }
}

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