package nodomain.pacjo.wear.watchface.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import nodomain.pacjo.wear.watchface.editor.WatchFaceConfigStateHolder

// TODO: do something about this
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