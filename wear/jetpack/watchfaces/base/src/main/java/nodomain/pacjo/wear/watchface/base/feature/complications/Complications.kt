package nodomain.pacjo.wear.watchface.base.feature.complications

import android.content.Context
import androidx.wear.watchface.CanvasComplicationFactory
import androidx.wear.watchface.ComplicationSlot
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.complications.rendering.CanvasComplicationDrawable
import androidx.wear.watchface.complications.rendering.ComplicationDrawable
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.WatchFaceLayer
import kotlinx.coroutines.CoroutineScope
import nodomain.pacjo.wear.watchface.base.feature.DrawableFeature
import nodomain.pacjo.wear.watchface.base.feature.FeatureFactory
import nodomain.pacjo.wear.watchface.base.feature.WatchFaceFeature
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
                        // Apply the custom style from the definition
                        // TODO: I'd like to do this:
                        // complicationDrawable.activeStyle = definition.complicationStyle
                        // but 'var cannot be re-assigned', so: - TODO: maybe use reflection?
                        complicationDrawable.activeStyle.apply {
                            backgroundColor = definition.activeStyle.backgroundColor
                            backgroundDrawable = definition.activeStyle.backgroundDrawable
                            textColor = definition.activeStyle.textColor
                            titleColor = definition.activeStyle.titleColor
                            setTextTypeface(definition.activeStyle.textTypeface)
                            setTitleTypeface(definition.activeStyle.titleTypeface)
                            imageColorFilter = definition.activeStyle.imageColorFilter
                            iconColor = definition.activeStyle.iconColor
                            textSize = definition.activeStyle.textSize
                            titleSize = definition.activeStyle.titleSize
                            borderColor = definition.activeStyle.borderColor
                            borderStyle = definition.activeStyle.borderStyle
                            borderDashWidth = definition.activeStyle.borderDashWidth
                            borderDashGap = definition.activeStyle.borderDashGap
                            borderRadius = definition.activeStyle.borderRadius
                            rangedValueRingWidth = definition.activeStyle.rangedValueRingWidth
                            rangedValuePrimaryColor = definition.activeStyle.rangedValuePrimaryColor
                            rangedValueSecondaryColor = definition.activeStyle.rangedValueSecondaryColor
                            highlightColor = definition.activeStyle.highlightColor
                        }
                        complicationDrawable.ambientStyle.apply {
                            backgroundColor = definition.ambientStyle.backgroundColor
                            backgroundDrawable = definition.ambientStyle.backgroundDrawable
                            textColor = definition.ambientStyle.textColor
                            titleColor = definition.ambientStyle.titleColor
                            setTextTypeface(definition.ambientStyle.textTypeface)
                            setTitleTypeface(definition.ambientStyle.titleTypeface)
                            imageColorFilter = definition.ambientStyle.imageColorFilter
                            iconColor = definition.ambientStyle.iconColor
                            textSize = definition.ambientStyle.textSize
                            titleSize = definition.ambientStyle.titleSize
                            borderColor = definition.ambientStyle.borderColor
                            borderStyle = definition.ambientStyle.borderStyle
                            borderDashWidth = definition.ambientStyle.borderDashWidth
                            borderDashGap = definition.ambientStyle.borderDashGap
                            borderRadius = definition.ambientStyle.borderRadius
                            rangedValueRingWidth = definition.ambientStyle.rangedValueRingWidth
                            rangedValuePrimaryColor = definition.ambientStyle.rangedValuePrimaryColor
                            rangedValueSecondaryColor = definition.ambientStyle.rangedValueSecondaryColor
                            highlightColor = definition.ambientStyle.highlightColor
                        }
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
                    .setEnabled(definition.isEnabled)
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

    override val layer: WatchFaceLayer = WatchFaceLayer.COMPLICATIONS

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