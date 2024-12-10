package nodomain.pacjo.wear.watchface.data.watchface

import nodomain.pacjo.wear.watchface.utils.DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT
import nodomain.pacjo.wear.watchface.utils.USE_CUSTOM_FONT_FOR_COMPLICATIONS_DEFAULT

/**
 * Represents all data needed to render a watch face.
 */
data class WatchFaceData(
    val activeColorStyle: ColorStyle = ColorStyle.STYLE1,
    val ambientColorStyle: ColorStyle = ColorStyle.AMBIENT,
    val font: Fonts = Fonts.DEFAULT,
    val useCustomFontForComplications: Boolean = USE_CUSTOM_FONT_FOR_COMPLICATIONS_DEFAULT,
    val drawComplicationsInAmbient: Boolean = DRAW_COMPLICATIONS_IN_AMBIENT_DEFAULT
)