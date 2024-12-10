package nodomain.pacjo.wear.watchface.editor

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import kotlinx.coroutines.yield
import nodomain.pacjo.wear.watchface.utils.COLOR_STYLE_SETTING
import nodomain.pacjo.wear.watchface.utils.DRAW_COMPLICATIONS_IN_AMBIENT_SETTING
import nodomain.pacjo.wear.watchface.utils.FONT_SETTING
import nodomain.pacjo.wear.watchface.utils.TIME_RING_CORNER_RADIUS_SETTING
import nodomain.pacjo.wear.watchface.utils.TIME_RING_WIDTH_SETTING
import nodomain.pacjo.wear.watchface.utils.USE_CUSTOM_FONT_FOR_COMPLICATIONS_SETTING
import java.time.Instant
import java.time.temporal.ChronoUnit

class WatchFaceConfigStateHolder(
    private val scope: CoroutineScope,
    private val activity: ComponentActivity
) {
    private lateinit var editorSession: EditorSession

    // Keys from Watch Face Data Structure
    private lateinit var colorStyleKey: UserStyleSetting.ListUserStyleSetting
    private lateinit var fontKey: UserStyleSetting.ListUserStyleSetting
    private lateinit var useCustomFontForComplicationsKey: UserStyleSetting.BooleanUserStyleSetting
    private lateinit var drawComplicationsInAmbientKey: UserStyleSetting.BooleanUserStyleSetting
    private lateinit var timeRingWidthKey: UserStyleSetting.DoubleRangeUserStyleSetting
    private lateinit var timeRingCornerRadiusKey: UserStyleSetting.DoubleRangeUserStyleSetting

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

                FONT_SETTING -> {
                    fontKey = setting as UserStyleSetting.ListUserStyleSetting
                }

                USE_CUSTOM_FONT_FOR_COMPLICATIONS_SETTING -> {
                    useCustomFontForComplicationsKey = setting as UserStyleSetting.BooleanUserStyleSetting
                }

                DRAW_COMPLICATIONS_IN_AMBIENT_SETTING -> {
                    drawComplicationsInAmbientKey = setting as UserStyleSetting.BooleanUserStyleSetting
                }

                TIME_RING_WIDTH_SETTING -> {
                    timeRingWidthKey = setting as UserStyleSetting.DoubleRangeUserStyleSetting
                }

                TIME_RING_CORNER_RADIUS_SETTING -> {
                    timeRingCornerRadiusKey = setting as UserStyleSetting.DoubleRangeUserStyleSetting
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
            Instant.now().truncatedTo(ChronoUnit.MINUTES).plusSeconds(59).plusMillis(999),
            complicationsPreviewData
        )

        val colorStyle =
            userStyle[colorStyleKey] as UserStyleSetting.ListUserStyleSetting.ListOption
        val font =
            userStyle[fontKey] as UserStyleSetting.ListUserStyleSetting.ListOption
        val customFontComplications =
            userStyle[useCustomFontForComplicationsKey] as UserStyleSetting.BooleanUserStyleSetting.BooleanOption
        val complicationsInAmbient =
            userStyle[drawComplicationsInAmbientKey] as UserStyleSetting.BooleanUserStyleSetting.BooleanOption
        val timeRingWidth =
            userStyle[timeRingWidthKey] as UserStyleSetting.DoubleRangeUserStyleSetting.DoubleRangeOption
        val timeRingCornerRadius =
            userStyle[timeRingCornerRadiusKey] as UserStyleSetting.DoubleRangeUserStyleSetting.DoubleRangeOption

        Log.d(TAG, "/new values: $colorStyle, $font, $complicationsInAmbient, $timeRingWidth, $timeRingCornerRadius")

        return UserStylesAndPreview(
            colorStyleId = colorStyle.id.toString(),
            fontId = font.id.toString(),
            customFontComplications = customFontComplications.value,
            complicationsInAmbient = complicationsInAmbient.value,
            timeRingWidth = timeRingWidth.value.toFloat(),
            timeRingCornerRadius = timeRingCornerRadius.value.toFloat(),
            previewImage = bitmap
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun setComplication(complicationLocation: Int) {
        scope.launch(Dispatchers.Main.immediate) {
            editorSession.openComplicationDataSourceChooser(complicationLocation)
        }
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

    fun setFont(newFontId: String) {
        setStyle(UserStyleSetting.Id(FONT_SETTING), newFontId)
    }

    fun useCustomFontForComplications(enabled: Boolean) {
        setUserStyleOption(
            useCustomFontForComplicationsKey,
            UserStyleSetting.BooleanUserStyleSetting.BooleanOption.from(enabled)
        )
    }

    fun setDrawComplicationsInAmbient(enabled: Boolean) {
        setUserStyleOption(
            drawComplicationsInAmbientKey,
            UserStyleSetting.BooleanUserStyleSetting.BooleanOption.from(enabled)
        )
    }

    fun setTimeRingWidth(width: Float) {
        setUserStyleOption(
            timeRingWidthKey,
            UserStyleSetting.DoubleRangeUserStyleSetting.DoubleRangeOption(width.toDouble())
        )
    }

    fun setTimeRingCornerRadius(radius: Float) {
        setUserStyleOption(
            timeRingCornerRadiusKey,
            UserStyleSetting.DoubleRangeUserStyleSetting.DoubleRangeOption(radius.toDouble())
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
        val fontId: String,
        val customFontComplications: Boolean,
        val complicationsInAmbient: Boolean,
        val timeRingWidth: Float,
        val timeRingCornerRadius: Float,
        val previewImage: Bitmap
    )

    companion object {
        private const val TAG = "WatchFaceConfigStateHolder"
    }
}