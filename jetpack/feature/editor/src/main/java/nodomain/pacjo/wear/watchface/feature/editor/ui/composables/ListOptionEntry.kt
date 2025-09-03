package nodomain.pacjo.wear.watchface.feature.editor.ui.composables

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toBitmap
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import androidx.wear.watchface.style.UserStyleSetting
import nodomain.pacjo.wear.watchface.feature.editor.ui.activities.EditorActivity

@Composable
fun ListOptionEntry(
    option: UserStyleSetting.Option,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    CompactChip(
        onClick = onClick,
        modifier = Modifier.Companion.fillMaxWidth(),
        label = { Text(option.id.toString() /* TODO: change to .displayName or similar*/) },
        icon = {
            // TODO: don't like this
            val iconBitmap =
                (option as UserStyleSetting.ListUserStyleSetting.ListOption).icon?.loadDrawable(
                    LocalContext.current
                )?.toBitmap()?.asImageBitmap()
            Log.d(EditorActivity.Companion.TAG, "icon: ${option.icon}")
            iconBitmap?.let {
                Icon(
                    bitmap = it,
                    contentDescription = option.displayName.toString(),
                    modifier = Modifier.Companion.size(ChipDefaults.IconSize)
                )
            }
        },
        colors = if (isSelected) {
            ChipDefaults.primaryChipColors()
        } else {
            ChipDefaults.secondaryChipColors()
        }
    )
}