package nodomain.pacjo.wear.watchface.data.watchface

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting
import nodomain.pacjo.wear.watchface.R

const val STYLE1_BACKGROUND_STYLE_ID = "style1_id"
private val STYLE1_COLOR_STYLE_NAME_RESOURCE_ID = R.string.background_style1_name

const val STYLE2_BACKGROUND_STYLE_ID = "style2_id"
private val STYLE2_COLOR_STYLE_NAME_RESOURCE_ID = R.string.background_style2_name

const val STYLE3_BACKGROUND_STYLE_ID = "style3_id"
private val STYLE3_COLOR_STYLE_NAME_RESOURCE_ID = R.string.background_style3_name

const val STYLE4_BACKGROUND_STYLE_ID = "style4_id"
private val STYLE4_COLOR_STYLE_NAME_RESOURCE_ID = R.string.background_style4_name

// TODO: fix docs
/**
 * Represents watch face color style options the user can select (includes the unique id, the
 * complication style resource id, and general watch face color style resource ids).
 *
 * The companion object offers helper functions to translate a unique string id to the correct enum
 * and convert all the resource ids to their correct resources (with the Context passed in). The
 * renderer will use these resources to render the actual colors and ComplicationDrawables of the
 * watch face.
 */
enum class BackgroundStyles(
    val id: String,
    @StringRes val nameResourceId: Int,
    @DrawableRes val backgroundDrawableRes: Int
) {
    STYLE1(
        id = STYLE1_BACKGROUND_STYLE_ID,
        nameResourceId = STYLE1_COLOR_STYLE_NAME_RESOURCE_ID,
        backgroundDrawableRes = R.drawable.snoopy1         // TODO: change
    ),

    STYLE2(
        id = STYLE2_BACKGROUND_STYLE_ID,
        nameResourceId = STYLE2_COLOR_STYLE_NAME_RESOURCE_ID,
        backgroundDrawableRes = R.drawable.snoopy2         // TODO: change
    ),

    STYLE3(
        id = STYLE3_BACKGROUND_STYLE_ID,
        nameResourceId = STYLE3_COLOR_STYLE_NAME_RESOURCE_ID,
        backgroundDrawableRes = R.drawable.snoopy3         // TODO: change
    ),

    STYLE4(
        id = STYLE4_BACKGROUND_STYLE_ID,
        nameResourceId = STYLE4_COLOR_STYLE_NAME_RESOURCE_ID,
        backgroundDrawableRes = R.drawable.snoopy4         // TODO: change
    );

    companion object {
        /**
         * Translates the string id to the correct ColorStyleIdAndResourceIds object.
         */
        fun getBackgroundStyleConfig(id: String): BackgroundStyles {
            return when (id) {
                STYLE1.id -> STYLE1
                STYLE2.id -> STYLE2
                STYLE3.id -> STYLE3
                STYLE4.id -> STYLE4
                else -> STYLE1
            }
        }

        /**
         * Returns a list of [UserStyleSetting.ListUserStyleSetting.ListOption] for all
         * ColorStyleIdAndResourceIds enums. The watch face settings APIs use this to set up
         * options for the user to select a style.
         */
        fun toOptionList(context: Context): List<ListUserStyleSetting.ListOption> {
            val backgroundStylesList = enumValues<BackgroundStyles>()

            return backgroundStylesList.map { style ->
                ListUserStyleSetting.ListOption(
                    UserStyleSetting.Option.Id(style.id),
                    context.resources,
                    style.nameResourceId,
                    style.nameResourceId,
                    null        // we don't use icons, TODO: but we should
                )
            }
        }
    }
}
