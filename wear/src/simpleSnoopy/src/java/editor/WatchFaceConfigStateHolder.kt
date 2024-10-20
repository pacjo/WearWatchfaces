package nodomain.pacjo.wear.watchface.editor

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.editor.EditorSession
import androidx.wear.watchface.style.UserStyle
import androidx.wear.watchface.style.UserStyleSchema
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.plus
import kotlinx.coroutines.yield
import nodomain.pacjo.wear.watchface.utils.BACKGROUND_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.COLOR_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.HANDS_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.SMOOTH_SECONDS_HAND_SETTING
import java.time.Instant

class WatchFaceConfigStateHolder(
    private val scope: CoroutineScope,
    private val activity: ComponentActivity
) {
    private lateinit var editorSession: EditorSession

    // Keys from Watch Face Data Structure
    private lateinit var colorStyleKey: UserStyleSetting.ListUserStyleSetting
    private lateinit var handsStyleKey: UserStyleSetting.ListUserStyleSetting
    private lateinit var backgroundStyleKey: UserStyleSetting.ListUserStyleSetting
    private lateinit var smoothSecondsHandKey: UserStyleSetting.BooleanUserStyleSetting

    private val highlightedElementFlow = MutableStateFlow<RenderParameters.HighlightedElement?>(null)

    val uiState: StateFlow<EditWatchFaceUiState> =
        flow<EditWatchFaceUiState> {
            editorSession = EditorSession.createOnWatchEditorSession(
                activity = activity
            )

            extractsUserStyles(editorSession.userStyleSchema)

            emitAll(
                combine(
                    editorSession.userStyle,
                    editorSession.complicationsPreviewData,
                    highlightedElementFlow
                ) { userStyle, complicationsPreviewData, highlightedElement ->
                    yield()
                    EditWatchFaceUiState.Success(
                        createWatchFacePreview(userStyle, complicationsPreviewData, highlightedElement)
                    )
                }
            )
        }
            .stateIn(
                scope + Dispatchers.Main.immediate,
                SharingStarted.Eagerly,
                EditWatchFaceUiState.Loading("Initializing")
            )

    private fun extractsUserStyles(userStyleSchema: UserStyleSchema) {
        // Loops through user styles and retrieves user editable styles.
        for (setting in userStyleSchema.rootUserStyleSettings) {
            when (setting.id.toString()) {
                COLOR_STYLE_SETTING -> {
                    colorStyleKey = setting as UserStyleSetting.ListUserStyleSetting
                }

                HANDS_STYLE_SETTING -> {
                    handsStyleKey = setting as UserStyleSetting.ListUserStyleSetting
                }

                BACKGROUND_STYLE_SETTING -> {
                    backgroundStyleKey = setting as UserStyleSetting.ListUserStyleSetting
                }

                SMOOTH_SECONDS_HAND_SETTING -> {
                    smoothSecondsHandKey = setting as UserStyleSetting.BooleanUserStyleSetting
                }
            }
        }
    }

    /* Creates a new bitmap render of the updated watch face and passes it along (with all the other
     * updated values) to the Activity to render.
     */
    private fun createWatchFacePreview(
        userStyle: UserStyle,
        complicationsPreviewData: Map<Int, ComplicationData>,
        highlightedElement: RenderParameters.HighlightedElement?
    ): UserStylesAndPreview {
        Log.d(TAG, "updatesWatchFacePreview()")

        val bitmap = editorSession.renderWatchFaceToBitmap(
            RenderParameters(
                DrawMode.INTERACTIVE,
                WatchFaceLayer.ALL_WATCH_FACE_LAYERS,
                highlightedElement?.let {
                    RenderParameters.HighlightLayer(
                        it,
                        Color.TRANSPARENT,        // highlight color
                        Color.argb(200, 0, 0, 0)  // darken everything else.
                    )
                }
            ),
            // or we could use `editorSession.previewReferenceInstant` for default
            Instant.now().plusSeconds(30 - Instant.now().epochSecond % 60),
            complicationsPreviewData
        )

        val colorStyle =
            userStyle[colorStyleKey] as UserStyleSetting.ListUserStyleSetting.ListOption
        val handsStyle =
            userStyle[handsStyleKey] as UserStyleSetting.ListUserStyleSetting.ListOption
        val backgroundStyle =
            userStyle[backgroundStyleKey] as UserStyleSetting.ListUserStyleSetting.ListOption
        val smoothSecondsHand =
            userStyle[smoothSecondsHandKey] as UserStyleSetting.BooleanUserStyleSetting.BooleanOption

        Log.d(TAG, "/new values: $colorStyle, $handsStyleKey, $smoothSecondsHandKey")

        return UserStylesAndPreview(
            colorStyleId = colorStyle.id.toString(),
            handsStyleId = handsStyle.id.toString(),
            backgroundStyleId = backgroundStyle.id.toString(),
            smoothSecondsHand = smoothSecondsHand.value,
            previewImage = bitmap
        )
    }

    fun setHighlightedElement(element: RenderParameters.HighlightedElement?) {
        highlightedElementFlow.value = element
    }

    private fun setStyle(styleSettingId: UserStyleSetting.Id, newStyleId: String) {
        val userStyleSettingList = editorSession.userStyleSchema.rootUserStyleSettings

        // Loops over all UserStyleSettings to find the matching setting for the given style ID
        for (userStyleSetting in userStyleSettingList) {
            if (userStyleSetting.id == styleSettingId) {
                val listUserStyleSetting = userStyleSetting as UserStyleSetting.ListUserStyleSetting

                // Loops over the UserStyleSetting options to find the matching option
                for (styleOption in listUserStyleSetting.options) {
                    if (styleOption.id.toString() == newStyleId) {
                        setUserStyleOption(listUserStyleSetting, styleOption)
                        return
                    }
                }
            }
        }
    }

    fun setColorStyle(newColorStyleId: String) {
        setStyle(UserStyleSetting.Id(COLOR_STYLE_SETTING), newColorStyleId)
    }

    fun setHandsStyle(newHandsStyleId: String) {
        setStyle(UserStyleSetting.Id(HANDS_STYLE_SETTING), newHandsStyleId)
    }

    fun setBackgroundsStyle(newBackgroundStyleId: String) {
        setStyle(UserStyleSetting.Id(BACKGROUND_STYLE_SETTING), newBackgroundStyleId)
    }

    fun setSmoothSecondsHand(enabled: Boolean) {
        setUserStyleOption(
            smoothSecondsHandKey,
            UserStyleSetting.BooleanUserStyleSetting.BooleanOption.from(enabled)
        )
    }

    // Saves User Style Option change back to the back to the EditorSession.
    // Note: The UI widgets in the Activity that can trigger this method (through the 'set' methods)
    // will only be enabled after the EditorSession has been initialized.
    private fun setUserStyleOption(
        userStyleSetting: UserStyleSetting,
        userStyleOption: UserStyleSetting.Option
    ) {
        Log.d(TAG, "setUserStyleOption()")
        Log.d(TAG, "\tuserStyleSetting: $userStyleSetting")
        Log.d(TAG, "\tuserStyleOption: $userStyleOption")

        val mutableUserStyle = editorSession.userStyle.value.toMutableUserStyle()
        mutableUserStyle[userStyleSetting] = userStyleOption
        editorSession.userStyle.value = mutableUserStyle.toUserStyle()
    }

    sealed class EditWatchFaceUiState {
        data class Success(val userStylesAndPreview: UserStylesAndPreview) : EditWatchFaceUiState()
        data class Loading(val message: String) : EditWatchFaceUiState()
        data class Error(val exception: Throwable) : EditWatchFaceUiState()
    }

    data class UserStylesAndPreview(
        val colorStyleId: String,
        val handsStyleId: String,
        val backgroundStyleId: String,
        val smoothSecondsHand: Boolean,
        val previewImage: Bitmap
    )

    companion object {
        private const val TAG = "WatchFaceConfigStateHolder"
    }
}
