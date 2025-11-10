package nodomain.pacjo.wear.watchface.feature.editor.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.wear.compose.foundation.pager.HorizontalPager
import androidx.wear.compose.foundation.pager.rememberPagerState
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material3.HorizontalPagerScaffold
import androidx.wear.watchface.style.UserStyleSetting
import kotlinx.serialization.Serializable
import nodomain.pacjo.wear.watchface.feature.editor.EditorStateHolder
import nodomain.pacjo.wear.watchface.feature.editor.EditorUiState
import nodomain.pacjo.wear.watchface.feature.editor.UserStylesAndPreview
import nodomain.pacjo.wear.watchface.feature.editor.ui.activities.EditorActivity

@Serializable
data object SettingsOverviewScreen

@Composable
fun SettingsOverviewScreen(uiState: EditorUiState, stateHolder: EditorStateHolder) {
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
                schema.rootUserStyleSettings
                    .filterIsInstance<UserStyleSetting.ListUserStyleSetting>()
                    .filter { it.options.size > 1 }     // hide page if only one option is available
            }
            val areComplicationsPresent = userStylesAndPreview.complicationSlotsStateMap.isNotEmpty()

            Log.d(EditorActivity.TAG, "root styles: ${schema.rootUserStyleSettings}")
            Log.d(EditorActivity.TAG, "list settings: $listSettings")

            Box(modifier = Modifier.fillMaxSize()) {
                // background preview
                Image(
                    bitmap = userStylesAndPreview.previewImage.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

                if (listSettings.isNotEmpty() || areComplicationsPresent) {
                    SettingsHorizontalPager(listSettings, userStylesAndPreview, stateHolder, areComplicationsPresent)
                }
            }
        }

        is EditorUiState.Error -> {
            EditorErrorScreen(uiState.exception.message)
        }
    }
}

// TODO: add boolean settings at the end
@Composable
private fun SettingsHorizontalPager(
    listSettings: List<UserStyleSetting.ListUserStyleSetting>,
    userStylesAndPreview: UserStylesAndPreview,
    stateHolder: EditorStateHolder,
    areComplicationsPresent: Boolean
) {
    val numberOfPages = listSettings.size + (if (areComplicationsPresent) 1 else 0)
    val pagerState = rememberPagerState(pageCount = { numberOfPages })

    HorizontalPagerScaffold(
        pagerState = pagerState
    ) {
        HorizontalPager(
            state = pagerState
        ) { currentPage ->
            when (currentPage) {
                // something like:
//                0 -> { /* colors */ }
//                1 -> { /* hands */ }
//                2 -> { /* complications */ }
//                3 -> { /* misc */ }

                // one of list settings pages
                in 0..<listSettings.size -> {
                    val setting = listSettings[currentPage]
                    GenericListSettingScreen(
                        listSetting = setting,
                        userStyle = userStylesAndPreview.userStyle
                    ) { optionId ->
                        stateHolder.setUserStyleOption(setting.id.toString(), optionId)
                    }
                }

                // last page - complications
                numberOfPages - 1 -> {
                    ComplicationsSettingScreen(
                        complicationSlotsStateMap = userStylesAndPreview.complicationSlotsStateMap
                    ) { complicationSlotId ->
                        stateHolder.openComplicationDataSourceChooser(complicationSlotId)
                    }
                }
            }
        }
    }
}