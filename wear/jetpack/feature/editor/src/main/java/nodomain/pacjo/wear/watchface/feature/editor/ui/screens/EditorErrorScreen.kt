package nodomain.pacjo.wear.watchface.feature.editor.ui.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ConfirmationDialogDefaults
import androidx.wear.compose.material3.FailureConfirmationDialog
import androidx.wear.compose.material3.confirmationDialogCurvedText

@Composable
fun EditorErrorScreen(errorMessage: String?) {
    val activity = LocalActivity.current
    val message = errorMessage ?: "Unknown error occurred"

    // TODO: think about proper icon
    val style = ConfirmationDialogDefaults.curvedTextStyle
    FailureConfirmationDialog(
        visible = true,
        onDismissRequest = {
            // close out of the activity
            activity!!.finish()
        },
        curvedText = { confirmationDialogCurvedText(message, style) },
    )
}