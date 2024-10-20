package nodomain.pacjo.wear.watchface.data.watchface

import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting
import nodomain.pacjo.wear.watchface.R

/**
 * Represents watch face color style options the user can select.
 */
enum class ColorStyle(
    val id: String,
    @StringRes val nameResourceId: Int,
    @ColorRes val primaryColorId: Int,
    @ColorRes val secondaryColorId: Int,
    @ColorRes val tertiaryColorId: Int,
    @ColorRes val outlineColorId: Int,
    @ColorRes val backgroundColorId: Int,
    @DrawableRes val iconResId: Int?
) {
    STYLE1(
        id = "style1_id",
        nameResourceId = R.string.style1_name,
        primaryColorId = R.color.style1_primary,
        secondaryColorId = R.color.style1_secondary,
        tertiaryColorId = R.color.style1_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background,
        iconResId = R.drawable.color_style1_icon
    ),

    STYLE2(
        id = "style2_id",
        nameResourceId = R.string.style2_name,
        primaryColorId = R.color.style2_primary,
        secondaryColorId = R.color.style2_secondary,
        tertiaryColorId = R.color.style2_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background,
        iconResId = R.drawable.color_style2_icon
    ),

    STYLE3(
        id = "style3_id",
        nameResourceId = R.string.style3_name,
        primaryColorId = R.color.style3_primary,
        secondaryColorId = R.color.style3_secondary,
        tertiaryColorId = R.color.style3_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background,
        iconResId = R.drawable.color_style3_icon
    ),

    STYLE4(
        id = "style4_id",
        nameResourceId = R.string.style4_name,
        primaryColorId = R.color.style4_primary,
        secondaryColorId = R.color.style4_secondary,
        tertiaryColorId = R.color.style4_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background,
        iconResId = R.drawable.color_style4_icon
    ),

    STYLE5(
        id = "style5_id",
        nameResourceId = R.string.style5_name,
        primaryColorId = R.color.style5_primary,
        secondaryColorId = R.color.style5_secondary,
        tertiaryColorId = R.color.style5_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background,
        iconResId = R.drawable.color_style5_icon
    ),

    STYLE6(
        id = "style6_id",
        nameResourceId = R.string.style6_name,
        primaryColorId = R.color.style6_primary,
        secondaryColorId = R.color.style6_secondary,
        tertiaryColorId = R.color.style6_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background,
        iconResId = R.drawable.color_style6_icon
    ),

    STYLE7(
        id = "style7_id",
        nameResourceId = R.string.style7_name,
        primaryColorId = R.color.style7_primary,
        secondaryColorId = R.color.style7_secondary,
        tertiaryColorId = R.color.style7_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background,
        iconResId = R.drawable.color_style7_icon
    ),

    STYLE8(
        id = "style8_id",
        nameResourceId = R.string.style8_name,
        primaryColorId = R.color.style8_primary,
        secondaryColorId = R.color.style8_secondary,
        tertiaryColorId = R.color.style8_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background,
        iconResId = R.drawable.color_style8_icon
    ),

    AMBIENT(
        id = "ambient_style_id",
        nameResourceId = R.string.ambient_style_name,
        primaryColorId = R.color.ambient_primary,
        secondaryColorId = R.color.ambient_secondary,
        tertiaryColorId = R.color.ambient_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background,
        iconResId = null
    );

    companion object {
        /**
         * Translates the string id to the correct ColorStyleIdAndResourceIds object.
         */
        fun getColorStyleConfig(id: String): ColorStyle {
            return when (id) {
                STYLE1.id -> STYLE1
                STYLE2.id -> STYLE2
                STYLE3.id -> STYLE3
                STYLE4.id -> STYLE4
                STYLE5.id -> STYLE5
                STYLE6.id -> STYLE6
                STYLE7.id -> STYLE7
                STYLE8.id -> STYLE8
                AMBIENT.id -> AMBIENT

                else -> STYLE1
            }
        }

        /**
         * Returns a list of [UserStyleSetting.ListUserStyleSetting.ListOption] for all
         * ColorStyleIdAndResourceIds enums. The watch face settings APIs use this to set up
         * options for the user to select a style.
         */
        fun toOptionList(context: Context): List<ListUserStyleSetting.ListOption> {
            val colorStyleList = enumValues<ColorStyle>()

            return colorStyleList
                .filter { colorStyle ->
                    // remove ambient style from the list since it's for internal use only
                    colorStyle.id != AMBIENT.id
                }
                .map { colorStyle ->
                    ListUserStyleSetting.ListOption(
                        UserStyleSetting.Option.Id(colorStyle.id),
                        context.resources,
                        colorStyle.nameResourceId,
                        colorStyle.nameResourceId,
                        colorStyle.iconResId?.let { Icon.createWithResource(context, colorStyle.iconResId) }
                    )
            }
        }
    }
}
