package nodomain.pacjo.wear.watchface.data.watchface

import android.content.Context
import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting
import nodomain.pacjo.wear.watchface.R

/**
 * Represents watch face background style options that user can select.
 *
 * For companion editor (phone app) style icon [iconDrawableResourceId] is used, unless
 * it's set to null in which case [backgroundDrawableResourceId] is used instead. Since
 * those are sent to the phone, they need to be rather small in size.
 */
enum class BackgroundStyles(
    val id: String,
    @StringRes val nameResourceId: Int,
    @DrawableRes val backgroundDrawableResourceId: Int,
    @DrawableRes val iconDrawableResourceId: Int?
) {
    STYLE1(
        id = "style1_id",
        nameResourceId = R.string.background_style1_name,
        backgroundDrawableResourceId = R.drawable.background_hearts,
        iconDrawableResourceId = R.drawable.background_hearts_icon
    ),

    STYLE2(
        id = "style2_id",
        nameResourceId = R.string.background_style2_name,
        backgroundDrawableResourceId = R.drawable.background_hugging,
        iconDrawableResourceId = R.drawable.background_hugging_icon
    ),

    STYLE3(
        id = "style3_id",
        nameResourceId = R.string.background_style3_name,
        backgroundDrawableResourceId = R.drawable.background_sleeping,
        iconDrawableResourceId = R.drawable.background_sleeping_icon
    ),

    STYLE4(
        id = "style4_id",
        nameResourceId = R.string.background_style4_name,
        backgroundDrawableResourceId = R.drawable.background_outdoors,
        iconDrawableResourceId = R.drawable.background_outdoors_icon
    );

    companion object {
        /**
         * Translates the string id to the correct [BackgroundStyles] object.
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
         * [BackgroundStyles] enums. The watch face settings APIs use this to set up
         * options for the user to select a style.
         */
        fun toOptionList(context: Context): List<ListUserStyleSetting.ListOption> {
            val stylesList = enumValues<BackgroundStyles>()

            return stylesList.map { style ->
                ListUserStyleSetting.ListOption(
                    UserStyleSetting.Option.Id(style.id),
                    context.resources,
                    style.nameResourceId,
                    style.nameResourceId,
                    style.iconDrawableResourceId?.run {
                        Icon.createWithResource(context, style.iconDrawableResourceId)
                    } ?: Icon.createWithResource(context, style.backgroundDrawableResourceId)
                )
            }
        }
    }
}
