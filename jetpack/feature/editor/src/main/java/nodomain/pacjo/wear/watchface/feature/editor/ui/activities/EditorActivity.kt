package nodomain.pacjo.wear.watchface.feature.editor.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.HorizontalPageIndicator
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.PageIndicatorState
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.Text
import androidx.wear.watchface.style.UserStyle
import androidx.wear.watchface.style.UserStyleSetting
import nodomain.pacjo.wear.watchface.feature.editor.EditorStateHolder
import nodomain.pacjo.wear.watchface.feature.editor.EditorUiState
import nodomain.pacjo.wear.watchface.feature.editor.UserStylesAndPreview
import nodomain.pacjo.wear.watchface.feature.editor.ui.theme.AppTheme

class EditorActivity : ComponentActivity() {
    private lateinit var stateHolder: EditorStateHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        stateHolder = EditorStateHolder(lifecycleScope, this)

        setContent {
            val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

            AppTheme {
                EditorScreen(uiState = uiState, stateHolder = stateHolder)

//                val maxPages = 4
//                var selectedPage by remember { mutableIntStateOf(0) }
//                var finalValue by remember { mutableIntStateOf(0) }
//
//                val animatedSelectedPage by animateFloatAsState(
//                    targetValue = selectedPage.toFloat(),
//                ) {
//                    finalValue = it.toInt()
//                }
//
//                val pageIndicatorState: PageIndicatorState = remember {
//                    object : PageIndicatorState {
//                        override val pageOffset: Float
//                            get() = animatedSelectedPage - finalValue
//                        override val selectedPage: Int
//                            get() = finalValue
//                        override val pageCount: Int
//                            get() = maxPages
//                    }
//                }
            }
        }
    }
}

@Composable
private fun EditorScreen(uiState: EditorUiState, stateHolder: EditorStateHolder) {
    when (uiState) {
        is EditorUiState.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize()
            )
        }

        is EditorUiState.Success -> {
            val userStylesAndPreview = uiState.userStylesAndPreview
            val schema = uiState.userStylesAndPreview.schema

            val listSettings = remember(schema) {
                schema.rootUserStyleSettings.filterIsInstance<UserStyleSetting.ListUserStyleSetting>()
            }

            Log.d("pacjodebug", "root styles: ${schema.rootUserStyleSettings}")
            Log.d("pacjodebug", "list settings: $listSettings")

            Box(modifier = Modifier.fillMaxSize()) {
                // background preview - TODO: dim?
                Image(
                    bitmap = userStylesAndPreview.previewImage.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

                if (listSettings.isNotEmpty()) {
                    SettingsHorizontalPager(listSettings, userStylesAndPreview, stateHolder)
                }
            }
        }

        is EditorUiState.Error -> {
            // TODO: change maybe?
            Text("Error: ${uiState.exception.message}")
        }
    }
}

// TODO: add boolean settings at the end
@Composable
private fun SettingsHorizontalPager(
    listSettings: List<UserStyleSetting.ListUserStyleSetting>,
    userStylesAndPreview: UserStylesAndPreview,
    stateHolder: EditorStateHolder
) {
    val horizontalPagerState = rememberPagerState { listSettings.size }
    val pageIndicatorState: PageIndicatorState = remember {
        object : PageIndicatorState {
            override val pageOffset: Float
                get() = 0f
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
            state = horizontalPagerState,
            modifier = Modifier.fillMaxSize()
        ) { currentPage ->
            val setting = listSettings[currentPage]
            LiseSettingPage(
                setting = setting,
                userStyle = userStylesAndPreview.userStyle
            ) { optionId ->
                stateHolder.setUserStyleOption(setting.id.toString(), optionId)
            }
//            when (currentPage) {
//                0 -> { /* colors */ }
//                1 -> { /* colors */ }
//                2 -> { /* complications */ }
//                3 -> { /* misc */ }
//            }
        }
    }
}

@Composable
private fun LiseSettingPage(
    setting: UserStyleSetting.ListUserStyleSetting,
    userStyle: UserStyle,
    onOptionClick: (String) -> Unit
) {
    val currentOptionId = userStyle[setting]?.toString()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = setting.displayName.toString(),
            style = MaterialTheme.typography.title3,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // A scrollable list for all the options of this setting
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(setting.options) { option ->
                OptionButton(
                    option = option,
                    isSelected = option.id.toString() == currentOptionId,
                    onClick = { onOptionClick(option.id.toString()) }
                )
            }
        }
    }
}

@Composable
fun OptionButton(
    option: UserStyleSetting.Option,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    CompactChip(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(option.id.toString() /* TODO: change to .displayName or similar*/) },
        icon = {
            // TODO: don't like this
            val iconBitmap = (option as UserStyleSetting.ListUserStyleSetting.ListOption).icon?.loadDrawable(LocalContext.current)?.toBitmap()?.asImageBitmap()
            Log.d("pacjodebug", "icon: ${option.icon}")
            iconBitmap?.let {
                    Icon(
                        bitmap = it,
                        contentDescription = option.displayName.toString(),
                        modifier = Modifier.size(ChipDefaults.IconSize)
                    )
                }
        },
        colors = if (isSelected) {
            ChipDefaults.primaryChipColors()
        } else {
            ChipDefaults.secondaryChipColors()
        }
    )
}


//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        installSplashScreen()
//
//        super.onCreate(savedInstanceState)
//
//        setTheme(android.R.style.Theme_DeviceDefault)
//
//        setContent {
//            WearApp("Android")
//        }
//    }
//}
//
//@Composable
//fun WearApp(greetingName: String) {
//    WearWatchfacesTheme {
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colors.background),
//            contentAlignment = Alignment.Center
//        ) {
//            TimeText()
//            Greeting(greetingName = greetingName)
//        }
//    }
//}
//
//@Composable
//fun Greeting(greetingName: String) {
//    Text(
//        modifier = Modifier.fillMaxWidth(),
//        textAlign = TextAlign.Center,
//        color = MaterialTheme.colors.primary,
//        text = stringResource(R.string.hello_world, greetingName)
//    )
//}
//
//@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
//@Composable
//fun DefaultPreview() {
//    WearApp("Preview Android")
//}