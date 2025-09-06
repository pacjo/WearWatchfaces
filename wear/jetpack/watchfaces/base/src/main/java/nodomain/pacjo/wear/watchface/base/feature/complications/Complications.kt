package nodomain.pacjo.wear.watchface.base.feature.complications

import android.content.Context
import androidx.wear.watchface.CanvasComplicationFactory
import androidx.wear.watchface.ComplicationSlot
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.complications.rendering.CanvasComplicationDrawable
import androidx.wear.watchface.complications.rendering.ComplicationDrawable
import androidx.wear.watchface.complications.rendering.ComplicationStyle
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSetting
import kotlinx.coroutines.CoroutineScope
import nodomain.pacjo.wear.watchface.base.feature.DrawableFeature
import nodomain.pacjo.wear.watchface.base.feature.FeatureFactory
import nodomain.pacjo.wear.watchface.base.feature.GranularWatchFaceLayer
import nodomain.pacjo.wear.watchface.base.feature.WatchFaceFeature
import nodomain.pacjo.wear.watchface.base.feature.complications.extensions.set
import nodomain.pacjo.wear.watchface.base.renderer.RenderingContext

class ComplicationsFeature(
    val definitions: List<ComplicationSlotDefinition>
) : FeatureFactory {

    constructor(
        block: () -> List<ComplicationSlotDefinition>
    ) : this(block())

    override fun getStyleSettings(context: Context): List<UserStyleSetting> {
        // TODO: think about it (drawComplicationsInAmbient (bool), ambientComplicationStyle (list) ...)
        return emptyList()
    }

    override fun create(
        context: Context,
        coroutineScope: CoroutineScope,
        currentUserStyleRepository: CurrentUserStyleRepository,
        watchState: WatchState
    ): WatchFaceFeature {
        return Complications()
    }

    companion object {
        fun createComplicationSlotsManager(
            context: Context,
            featureFactories: List<FeatureFactory>,
            currentUserStyleRepository: CurrentUserStyleRepository
        ): ComplicationSlotsManager {
            val definitions = featureFactories
                .filterIsInstance<ComplicationsFeature>()
                .flatMap { factory -> factory.definitions }

            if (definitions.isEmpty()) {
                // Return empty ComplicationSlotsManager if no complications are defined
                return ComplicationSlotsManager(emptyList(), currentUserStyleRepository)
            }

            val complicationSlots = definitions.map { definition ->
                val canvasComplicationFactory =
                    CanvasComplicationFactory { watchState, listener ->
                        val complicationDrawable = ComplicationDrawable(context)
                        // I'd like to do this:
                        // complicationDrawable.activeStyle = definition.complicationStyle
                        // but 'var cannot be re-assigned', so:
                        fun ComplicationStyle.set(complicationStyle: ComplicationStyle) {
                            backgroundColor = complicationStyle.backgroundColor
                            backgroundDrawable = complicationStyle.backgroundDrawable
                            textColor = complicationStyle.textColor
                            titleColor = complicationStyle.titleColor
                            setTextTypeface(complicationStyle.textTypeface)
                            setTitleTypeface(complicationStyle.titleTypeface)
                            imageColorFilter = complicationStyle.imageColorFilter
                            iconColor = complicationStyle.iconColor
                            textSize = complicationStyle.textSize
                            titleSize = complicationStyle.titleSize
                            borderColor = complicationStyle.borderColor
                            borderStyle = complicationStyle.borderStyle
                            borderDashWidth = complicationStyle.borderDashWidth
                            borderDashGap = complicationStyle.borderDashGap
                            borderRadius = complicationStyle.borderRadius
                            rangedValueRingWidth = complicationStyle.rangedValueRingWidth
                            rangedValuePrimaryColor = complicationStyle.rangedValuePrimaryColor
                            rangedValueSecondaryColor = complicationStyle.rangedValueSecondaryColor
                            highlightColor = complicationStyle.highlightColor
                        }

                        complicationDrawable.activeStyle.set(definition.activeStyle)
                        complicationDrawable.ambientStyle.set(definition.ambientStyle)

                        CanvasComplicationDrawable(
                            complicationDrawable,
                            watchState,
                            listener
                        )
                    }

                ComplicationSlot.createRoundRectComplicationSlotBuilder(
                    id = definition.id,
                    canvasComplicationFactory = canvasComplicationFactory,
                    supportedTypes = definition.supportedTypes,
                    defaultDataSourcePolicy = definition.defaultDataSourcePolicy,
                    bounds = definition.toComplicationSlotBounds()
                )
                    .build()
            }

            return ComplicationSlotsManager(
                complicationSlots,
                currentUserStyleRepository
            )
        }

        /**
         * Connects ComplicationsFeature instances with the ComplicationSlotsManager.
         * Called after creating features but before using them for rendering.
         *
         * @param features List of all watch face features
         * @param complicationSlotsManager The ComplicationSlotsManager from createComplicationSlotsManager
         */
        fun connectComplicationFeatures(
            features: List<WatchFaceFeature>,
            complicationSlotsManager: ComplicationSlotsManager
        ) {
            features
                .filterIsInstance<Complications>()
                .forEach { feature ->
                    feature.setComplicationSlotsManager(complicationSlotsManager)
                }
        }
    }
}

class Complications : DrawableFeature {

    override val layer = GranularWatchFaceLayer.COMPLICATIONS

    private var complicationSlotsManager: ComplicationSlotsManager? = null

    /**
     * Sets the ComplicationSlotsManager for this feature.
     * This should be called by the WatchFaceService after creating the ComplicationSlotsManager.
     */
    fun setComplicationSlotsManager(complicationSlotsManager: ComplicationSlotsManager) {
        this.complicationSlotsManager = complicationSlotsManager
    }

    override fun draw(renderingContext: RenderingContext) {
        // Complications are primarily 2D and work best with Canvas rendering
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            complicationSlotsManager?.let { manager ->
                manager.complicationSlots.values.forEach { complicationSlot ->
                    complicationSlot.render(
                        canvas = canvas,
                        zonedDateTime = zonedDateTime,
                        renderParameters = renderingContext.renderParameters
                    )
                }
            }
        }
        // TODO: support opengl
    }
}