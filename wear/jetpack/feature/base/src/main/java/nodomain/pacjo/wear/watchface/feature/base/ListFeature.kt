package nodomain.pacjo.wear.watchface.feature.base

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import androidx.annotation.StringRes
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toIcon
import androidx.core.graphics.toRectF
import androidx.wear.watchface.RenderParameters
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import nodomain.pacjo.wear.watchface.feature.rendering.CanvasRendererBackend
import nodomain.pacjo.wear.watchface.feature.rendering.GranularWatchFaceLayer
import nodomain.pacjo.wear.watchface.feature.rendering.RenderingContext
import java.time.ZonedDateTime
import kotlin.math.min

/**
 * A contract for any option that can be displayed in a ListFeature.
 *
 * @see ListFeature
 * @see RenderingContext
 */
interface FeatureOption {
    /**
     * Unique identifier for this option.
     */
    val id: String

    /**
     * String resource ID for the display name shown to users.
     */
    @get:StringRes
    val displayNameResourceId: Int

    /**
     * Draws a preview of this option for the settings UI.
     *
     * @param renderingContext context containing Canvas backend and current time.
     *
     * @see RenderingContext
     * @see RenderingContext.ifCanvas
     */
    fun drawPreview(renderingContext: RenderingContext)
}

/**
 * A specialized WatchFaceFeature for presenting a list of user-selectable options.
 *
 * @param T type of FeatureOption this feature manages
 *
 * @see FeatureOption
 * @see DrawableFeature
 * @see ListFeatureFactory
 */
abstract class ListFeature<T : FeatureOption> : WatchFaceFeature {
    /**
     * Unique identifier for this feature in the style system.
     */
    abstract val featureId: String

    /**
     * String resource ID for the feature name shown to users.
     */
    @get:StringRes
    abstract val featureDisplayNameResourceId: Int

    /**
     * String resource ID for the feature description shown to users.
     */
    @get:StringRes
    abstract val featureDescriptionResourceId: Int

    /**
     * The list of available options for this feature.
     *
     * The first option serves as the default when no user preference is saved.
     */
    abstract val options: List<T>

    /**
     * StateFlow providing access to the currently selected option.
     * It is initialized via the initialize() method.
     */
    lateinit var current: StateFlow<T>
        private set // prevent outside modification

    /**
     * Initialize the feature with runtime dependencies.
     *
     * @param scope [CoroutineScope] for managing the [StateFlow] lifecycle
     * @param currentUserStyleRepository repository providing access to user style preferences
     */
    fun initialize(
        scope: CoroutineScope,
        currentUserStyleRepository: CurrentUserStyleRepository
    ) {
        current = currentUserStyleRepository.userStyle.map { userStyle ->
            val styleId =
                userStyle[UserStyleSetting.Id(featureId)]?.toString() ?: options.first().id
            options.first { it.id == styleId }
        }.stateIn(
            scope,
            SharingStarted.Eagerly,
            options.first()
        )
    }
}

/**
 * Factory for creating ListFeature implementations with dependency injection.
 *
 * @param T type of FeatureOption managed by the created ListFeature
 *
 * @see ListFeature
 * @see FeatureFactory
 */
open class ListFeatureFactory<T : FeatureOption>(
    private val featureId: String,
    @StringRes private val featureDisplayNameResourceId: Int,
    @StringRes private val featureDescriptionResourceId: Int,
    private val options: List<T>,

    /**
     * Lambda function that creates the specific ListFeature instance.
     */
    private val featureCreator: (
        scope: CoroutineScope,
        repo: CurrentUserStyleRepository,
        options: List<T>
    ) -> ListFeature<T>
) : FeatureFactory {

    final override fun getStyleSettings(context: Context): List<UserStyleSetting> {
        return generateStyleSettings(
            context = context,
            featureId = featureId,
            featureDisplayNameResourceId = featureDisplayNameResourceId,
            featureDescriptionResourceId = featureDescriptionResourceId,
            options = options
        )
    }

    /**
     * Creates and initializes the ListFeature instance.
     *
     * @param context Android context for resource access
     * @param coroutineScope CoroutineScope for StateFlow management
     * @param currentUserStyleRepository Repository for style system integration
     * @param watchState Watch state information (not currently used by ListFeatures)
     * @return Initialized ListFeature ready for use
     */
    final override fun create(
        context: Context,
        coroutineScope: CoroutineScope,
        currentUserStyleRepository: CurrentUserStyleRepository,
        watchState: WatchState
    ): WatchFaceFeature {
        val feature = featureCreator(coroutineScope, currentUserStyleRepository, options)
        feature.initialize(coroutineScope, currentUserStyleRepository)

        return feature
    }
}

private fun <T : FeatureOption> generateStyleSettings(
    context: Context,
    featureId: String,
    @StringRes featureDisplayNameResourceId: Int,
    @StringRes featureDescriptionResourceId: Int,
    options: List<T>
): List<UserStyleSetting> {
    val settingOptions = options.map { option ->
        val displayMetrics = context.resources.displayMetrics
        val maxSize = 400       // maximum size enforced by Wear OS
        val width = min(displayMetrics.widthPixels, maxSize)
        val height = min(displayMetrics.heightPixels, maxSize)

        val previewBitmap = createBitmap(width, height)
        val canvas = Canvas(previewBitmap)
        val bounds = Rect(0, 0, width, height)
        val previewTime = ZonedDateTime.now()

        // clip to screen shape, otherwise just round corners a bit
        val radiusFraction = if (context.resources.configuration.isScreenRound) {
            0.5f
        } else {
            0.1f
        }
        val path = Path()
        path.addRoundRect(
            bounds.toRectF(),
            bounds.width() * radiusFraction,
            bounds.height() * radiusFraction,
            Path.Direction.CW
        )
        canvas.clipPath(path)

        // prepare RenderingContext - canvas only for now - TODO: add support for opengl
        val canvasBackend = object : CanvasRendererBackend {
            override val canvas = canvas
            override val bounds = bounds
        }

        // actual drawing - TODO: maybe there's a better way
        GranularWatchFaceLayer.entries.forEach { layer ->
            val renderingContext = RenderingContext(canvasBackend, previewTime, RenderParameters.DEFAULT_INTERACTIVE, layer)
            option.drawPreview(renderingContext)
        }

        ListUserStyleSetting.ListOption(
            UserStyleSetting.Option.Id(option.id),
            context.resources,
            option.displayNameResourceId,
            option.displayNameResourceId,
            previewBitmap.toIcon()
        )
    }

    return listOf(
        ListUserStyleSetting(
            UserStyleSetting.Id(featureId),
            context.resources,
            featureDisplayNameResourceId,
            featureDescriptionResourceId,
            icon = null,
            options = settingOptions,
            WatchFaceLayer.ALL_WATCH_FACE_LAYERS        // TODO: make configurable per feature
        )
    )
}