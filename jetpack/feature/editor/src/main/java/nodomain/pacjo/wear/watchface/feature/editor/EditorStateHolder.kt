package nodomain.pacjo.wear.watchface.feature.editor

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.editor.EditorSession
import androidx.wear.watchface.style.UserStyle
import androidx.wear.watchface.style.UserStyleSchema
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus

sealed class EditorUiState {
    data class Success(val userStylesAndPreview: UserStylesAndPreview) : EditorUiState()
    data class Loading(val message: String) : EditorUiState()
    data class Error(val exception: Throwable) : EditorUiState()
}

// Data class holding all the data the UI needs to render
data class UserStylesAndPreview(
    val previewImage: Bitmap,
    val schema: UserStyleSchema,
    val userStyle: UserStyle
)

class EditorStateHolder(
    private val scope: CoroutineScope,
    private val activity: ComponentActivity
) {
    private lateinit var editorSession: EditorSession

    val uiState: StateFlow<EditorUiState> = flow<EditorUiState> {
        editorSession = EditorSession.createOnWatchEditorSession(activity = activity)
        emitAll(
            editorSession.userStyle.map { userStyle ->
                EditorUiState.Success(createWatchFacePreview(userStyle))
            }
        )
    }.stateIn(
        scope + Dispatchers.Main.immediate,
        SharingStarted.Eagerly,
        EditorUiState.Loading("Initializing...")
    )

    private fun createWatchFacePreview(userStyle: UserStyle): UserStylesAndPreview {
        val bitmap = editorSession.renderWatchFaceToBitmap(
            RenderParameters(DrawMode.INTERACTIVE, WatchFaceLayer.ALL_WATCH_FACE_LAYERS),
            editorSession.previewReferenceInstant,
            null
        )
        return UserStylesAndPreview(
            previewImage = bitmap,
            schema = editorSession.userStyleSchema,
            userStyle = userStyle
        )
    }

    fun setUserStyleOption(settingId: String, optionId: String) {
        val setting = editorSession.userStyleSchema.rootUserStyleSettings
            .first { it.id.toString() == settingId } as? UserStyleSetting.ListUserStyleSetting
            ?: return

        val option = setting.options
            .first { it.id.toString() == optionId } as UserStyleSetting.ListUserStyleSetting.ListOption

        scope.launch(Dispatchers.Main.immediate) {
            val mutableUserStyle = editorSession.userStyle.value.toMutableUserStyle()
            mutableUserStyle[setting] = option
            editorSession.userStyle.value = mutableUserStyle.toUserStyle()
        }
    }
}