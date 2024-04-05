package nodomain.pacjo.wear.watchface.editor

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.style.UserStyleSetting
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds.Companion.getColorStyleConfig
import nodomain.pacjo.wear.watchface.editor.screens.ColorSelectScreen
import nodomain.pacjo.wear.watchface.editor.screens.ComplicationConfigScreen
import nodomain.pacjo.wear.watchface.editor.screens.MiscConfigScreen
import nodomain.pacjo.wear.watchface.editor.screens.PreferenceSwitch
import nodomain.pacjo.wear.watchface.editor.screens.TimeRingSettingsScreen
import nodomain.pacjo.wear.watchface.utils.CategorySelectButton
import nodomain.pacjo.wear.watchface.utils.TIME_RING_WIDTH_SETTING
import nodomain.pacjo.wear.watchface.utils.USELESS_SETTING_USED_FOR_PREVIEW_SETTING
import nodomain.pacjo.wear.watchface.utils.watchFacePreview

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
            val context = LocalContext.current

            val uiState = watchFacePreview(stateHolder)
            val bitmap = uiState?.previewImage?.asImageBitmap()

            val currentTheme = context.resources.getString(
                uiState?.colorStyleId?.let { getColorStyleConfig(it).nameResourceId } ?: R.string.colors_style_setting
            )

            val navController = rememberSwipeDismissableNavController()

            SwipeDismissableNavHost(
                navController = navController,
                startDestination = "category_select"
            ) {
                composable("category_select") {
                    MaterialTheme {
                        // background with animation
                        Crossfade(
                            targetState = bitmap,
                            modifier = Modifier.fillMaxSize(),
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = LinearEasing
                            ),
                            label = "watchface_preview_change"
                        ) { currentBitmap ->
                            if (currentBitmap != null) {
                                Image(
                                    bitmap = currentBitmap,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
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
                        LaunchedEffect(horizontalPagerState) {
                            snapshotFlow { horizontalPagerState.currentPage }.collect { page ->
                                when (page) {
                                    1 -> stateHolder.setHighlightedElement(
                                        RenderParameters.HighlightedElement.UserStyle(UserStyleSetting.Id(TIME_RING_WIDTH_SETTING))
                                    )
                                    2 -> stateHolder.setHighlightedElement(
                                        RenderParameters.HighlightedElement.AllComplicationSlots
                                    )
                                    // set to unused value, to dim whole face
                                    3 -> stateHolder.setHighlightedElement(
                                        RenderParameters.HighlightedElement.UserStyle(UserStyleSetting.Id(USELESS_SETTING_USED_FOR_PREVIEW_SETTING))
                                    )
                                    else -> stateHolder.setHighlightedElement(null)
                                }
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
                                    0 -> CategorySelectButton(context, currentTheme) {
                                            navController.navigate(
                                                "colors"
                                            )
                                        }
                                    1 -> CategorySelectButton(context, context.resources.getString(R.string.time_ring_setting)) {
                                            navController.navigate(
                                                "time_ring"
                                            )
                                        }
                                    2 -> ComplicationConfigScreen(stateHolder)
                                    3 -> MiscConfigScreen(
                                        listOf {
                                            PreferenceSwitch(
                                                text = context.resources.getString(R.string.misc_complications_on_aod),
                                                value = uiState!!.complicationsInAmbient,
                                                onCheckedChange = { checked ->
                                                    stateHolder.setDrawComplicationsInAmbient(
                                                        checked
                                                    )
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
                composable("colors") {
                    ColorSelectScreen(context, stateHolder, navController)
                }
                composable("time_ring") {
                    TimeRingSettingsScreen(context, stateHolder, uiState!!)
                }
            }
        }
    }
}