package nodomain.pacjo.wear.watchface.data.watchface

import android.content.Context
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting
import nodomain.pacjo.wear.watchface.R

const val AMBIENT_COLOR_STYLE_ID = "ambient_style_id"
private val AMBIENT_COLOR_STYLE_NAME_RESOURCE_ID = R.string.ambient_style_name

const val STYLE1_COLOR_STYLE_ID = "style1_id"
private val STYLE1_COLOR_STYLE_NAME_RESOURCE_ID = R.string.style1_name

const val STYLE2_COLOR_STYLE_ID = "style2_id"
private val STYLE2_COLOR_STYLE_NAME_RESOURCE_ID = R.string.style2_name

const val STYLE3_COLOR_STYLE_ID = "style3_id"
private val STYLE3_COLOR_STYLE_NAME_RESOURCE_ID = R.string.style3_name

const val STYLE4_COLOR_STYLE_ID = "style4_id"
private val STYLE4_COLOR_STYLE_NAME_RESOURCE_ID = R.string.style4_name

const val STYLE5_COLOR_STYLE_ID = "style5_id"
private val STYLE5_COLOR_STYLE_NAME_RESOURCE_ID = R.string.style5_name

const val STYLE6_COLOR_STYLE_ID = "style6_id"
private val STYLE6_COLOR_STYLE_NAME_RESOURCE_ID = R.string.style6_name

const val STYLE7_COLOR_STYLE_ID = "style7_id"
private val STYLE7_COLOR_STYLE_NAME_RESOURCE_ID = R.string.style7_name

/**
 * Represents watch face color style options the user can select (includes the unique id, the
 * complication style resource id, and general watch face color style resource ids).
 *
 * The companion object offers helper functions to translate a unique string id to the correct enum
 * and convert all the resource ids to their correct resources (with the Context passed in). The
 * renderer will use these resources to render the actual colors and ComplicationDrawables of the
 * watch face.
 */
enum class ColorStyleIdAndResourceIds(
    val id: String,
    @StringRes val nameResourceId: Int,
    @ColorRes val primaryColorId: Int,
    @ColorRes val secondaryColorId: Int,
    @ColorRes val tertiaryColorId: Int,
    @ColorRes val outlineColorId: Int,
    @ColorRes val backgroundColorId: Int
) {
    STYLE1(
        id = STYLE1_COLOR_STYLE_ID,
        nameResourceId = STYLE1_COLOR_STYLE_NAME_RESOURCE_ID,
        primaryColorId = R.color.style1_primary,
        secondaryColorId = R.color.style1_secondary,
        tertiaryColorId = R.color.style1_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background
    ),

    STYLE2(
        id = STYLE2_COLOR_STYLE_ID,
        nameResourceId = STYLE2_COLOR_STYLE_NAME_RESOURCE_ID,
        primaryColorId = R.color.style2_primary,
        secondaryColorId = R.color.style2_secondary,
        tertiaryColorId = R.color.style2_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background
    ),

    STYLE3(
        id = STYLE3_COLOR_STYLE_ID,
        nameResourceId = STYLE3_COLOR_STYLE_NAME_RESOURCE_ID,
        primaryColorId = R.color.style3_primary,
        secondaryColorId = R.color.style3_secondary,
        tertiaryColorId = R.color.style3_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background
    ),

    STYLE4(
        id = STYLE4_COLOR_STYLE_ID,
        nameResourceId = STYLE4_COLOR_STYLE_NAME_RESOURCE_ID,
        primaryColorId = R.color.style4_primary,
        secondaryColorId = R.color.style4_secondary,
        tertiaryColorId = R.color.style4_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background
    ),

    STYLE5(
        id = STYLE5_COLOR_STYLE_ID,
        nameResourceId = STYLE5_COLOR_STYLE_NAME_RESOURCE_ID,
        primaryColorId = R.color.style5_primary,
        secondaryColorId = R.color.style5_secondary,
        tertiaryColorId = R.color.style5_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background
    ),

    STYLE6(
        id = STYLE6_COLOR_STYLE_ID,
        nameResourceId = STYLE6_COLOR_STYLE_NAME_RESOURCE_ID,
        primaryColorId = R.color.style6_primary,
        secondaryColorId = R.color.style6_secondary,
        tertiaryColorId = R.color.style6_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background
    ),

    STYLE7(
        id = STYLE7_COLOR_STYLE_ID,
        nameResourceId = STYLE7_COLOR_STYLE_NAME_RESOURCE_ID,
        primaryColorId = R.color.style7_primary,
        secondaryColorId = R.color.style7_secondary,
        tertiaryColorId = R.color.style7_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background
    ),

    AMBIENT(
        id = AMBIENT_COLOR_STYLE_ID,
        nameResourceId = AMBIENT_COLOR_STYLE_NAME_RESOURCE_ID,
        primaryColorId = R.color.ambient_primary,
        secondaryColorId = R.color.ambient_secondary,
        tertiaryColorId = R.color.ambient_tertiary,
        outlineColorId = R.color.outline,
        backgroundColorId = R.color.background
    );

    companion object {
        /**
         * Translates the string id to the correct ColorStyleIdAndResourceIds object.
         */
        fun getColorStyleConfig(id: String): ColorStyleIdAndResourceIds {
            return when (id) {
                STYLE1.id -> STYLE1
                STYLE2.id -> STYLE2
                STYLE3.id -> STYLE3
                STYLE4.id -> STYLE4
                STYLE5.id -> STYLE5
                STYLE6.id -> STYLE6
                STYLE7.id -> STYLE7
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
            val colorStyleIdAndResourceIdsList = enumValues<ColorStyleIdAndResourceIds>()

            return colorStyleIdAndResourceIdsList.map { colorStyleIdAndResourceIds ->
                ListUserStyleSetting.ListOption(
                    UserStyleSetting.Option.Id(colorStyleIdAndResourceIds.id),
                    context.resources,
                    colorStyleIdAndResourceIds.nameResourceId,
                    colorStyleIdAndResourceIds.nameResourceId,
                    null        // we don't use icons
                )
            }
        }
    }
}
