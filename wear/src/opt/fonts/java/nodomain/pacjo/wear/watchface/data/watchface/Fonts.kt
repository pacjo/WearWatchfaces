package nodomain.pacjo.wear.watchface.data.watchface

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Icon
import androidx.annotation.FontRes
import androidx.annotation.StringRes
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting
import nodomain.pacjo.wear.watchface.R
import nodomain.pacjo.wear.watchface.utils.drawTextCentredBoth

enum class Fonts(
    val id: String,
    @StringRes val nameResourceId: Int,
    @FontRes val fontResourceId: Int? = null            // null for default system font
) {
    DEFAULT(
        id = "style_1_id",
        nameResourceId = R.string.font1_name
    ),

    TIMES_NEW_ROMAN(
        id = "style_2_id",
        nameResourceId = R.string.font2_name,
        fontResourceId = R.font.times_new_roman
    ),

    COMIC_SANS(
        id = "style_3_id",
        nameResourceId = R.string.font3_name,
        fontResourceId = R.font.comic_sans_ms
    ),

    IBM_PLEX(
        id = "style_4_id",
        nameResourceId = R.string.font4_name,
        fontResourceId = R.font.ibm_plex_sans
    ),

    NASALIZATION(
        id = "style_5_id",
        nameResourceId = R.string.font5_name,
        fontResourceId = R.font.nasalization
    ),

    NOTHING_DOT_57(
        id = "style_6_id",
        nameResourceId = R.string.font6_name,
        fontResourceId = R.font.nothing_dot_57
    );

    companion object {
        /**
         * Helper method to generate previews on the fly
         */
        fun createPreviewBitmap(
            context: Context,
            style: Fonts
        ): Bitmap {
            val width = 100
            val height = width

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            val paint = Paint().apply {
                textSize = height / 2f
                isAntiAlias = true
                color = Color.WHITE
                style.fontResourceId?.let { typeface = context.resources.getFont(it) }
            }

            drawTextCentredBoth(
                canvas = canvas,
                paint = paint,
                text = "Aa",
                cx = width / 2f,
                cy = height / 2f
            )

            return bitmap
        }

        /**
         * Translates the string id to the correct [Fonts] object.
         */
        fun getFontConfig(id: String): Fonts {
            return when (id) {
                DEFAULT.id -> DEFAULT
                TIMES_NEW_ROMAN.id -> TIMES_NEW_ROMAN
                COMIC_SANS.id -> COMIC_SANS
                IBM_PLEX.id -> IBM_PLEX
                NASALIZATION.id -> NASALIZATION
                NOTHING_DOT_57.id -> NOTHING_DOT_57

                else -> DEFAULT
            }
        }

        /**
         * Returns a list of [UserStyleSetting.ListUserStyleSetting.ListOption] for all
         * [Fonts] enums. The watch face settings APIs use this to set up
         * options for the user to select a style.
         */
        fun toOptionList(context: Context): List<ListUserStyleSetting.ListOption> {
            val stylesList = enumValues<Fonts>()

            return stylesList.map { style ->
                ListUserStyleSetting.ListOption(
                    UserStyleSetting.Option.Id(style.id),
                    context.resources,
                    style.nameResourceId,
                    style.nameResourceId,
                    Icon.createWithBitmap(createPreviewBitmap(context, style))
                )
            }
        }
    }
}
