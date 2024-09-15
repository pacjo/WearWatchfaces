package nodomain.pacjo.wear.watchface.editor.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import nodomain.pacjo.wear.watchface.data.watchface.ColorStyleIdAndResourceIds

@Composable
fun CategorySelectButton(context: Context, text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .background(
                    Color(context.getColor(ColorStyleIdAndResourceIds.AMBIENT.outlineColorId)),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text)
        }
    }
}