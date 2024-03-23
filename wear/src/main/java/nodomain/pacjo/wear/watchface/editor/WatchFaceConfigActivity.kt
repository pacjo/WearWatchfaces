package nodomain.pacjo.wear.watchface.editor

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds
import nodomain.pacjo.wear.watchface.editor.screens.ColorSelectScreen
import nodomain.pacjo.wear.watchface.editor.screens.ComplicationConfigScreen
import nodomain.pacjo.wear.watchface.editor.screens.MiscConfigScreen
import nodomain.pacjo.wear.watchface.editor.screens.TimeRingSettingsScreen

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

            val navController = rememberSwipeDismissableNavController()

            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "category_select"
            ) {
                composable("category_select") {
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
                                    0 -> CategorySelectButton(context, context.resources.getString(R.string.colors_style_setting)) {
                                            navController.navigate(
                                                "colors"
                                            )
                                        }
                                    1 -> CategorySelectButton(context, context.resources.getString(R.string.time_ring_setting)) {
                                            navController.navigate(
                                                "time_ring"
                                            )
                                        }
                                    2 -> CategorySelectButton(context, context.resources.getString(R.string.complication_setting)) {
                                            navController.navigate(
                                                "complications"
                                            )
                                        }
                                    3 -> MiscConfigScreen(context, stateHolder, uiState!!)
                                }
                            }
                        }
                    }
                }
                composable("colors") {
                    ColorSelectScreen(context, stateHolder)
                }
                composable("time_ring") {
                    TimeRingSettingsScreen(context, stateHolder, uiState!!)
                }
                composable("complications") {
                    ComplicationConfigScreen(stateHolder)
                }
            }
        }
    }
}

// TODO: finish
@Composable
fun watchFacePreview(stateHolder: WatchFaceConfigStateHolder): WatchFaceConfigStateHolder.UserStylesAndPreview? {     // TODO: change name
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