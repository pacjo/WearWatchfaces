package nodomain.pacjo.wear.watchface.feature.editor.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import nodomain.pacjo.wear.watchface.feature.editor.EditorStateHolder
import nodomain.pacjo.wear.watchface.feature.editor.ui.screens.SettingsOverviewScreen
import nodomain.pacjo.wear.watchface.feature.editor.ui.theme.AppTheme

class EditorActivity : ComponentActivity() {
    private lateinit var stateHolder: EditorStateHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_DeviceDefault)

        stateHolder = EditorStateHolder(lifecycleScope, this)

        setContent {
            val uiState by stateHolder.uiState.collectAsStateWithLifecycle()
            val navController = rememberSwipeDismissableNavController()

            AppTheme {
                // TODO: convert to type-safe navigation (i.e. remove toString())
                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = SettingsOverviewScreen.toString()
                ) {
                    composable(SettingsOverviewScreen.toString()) {
                        SettingsOverviewScreen(uiState = uiState, stateHolder = stateHolder)
                    }

                    // TODO: implement
//                    composable(GenericListSettingScreen.toString()) { backStackEntry ->
//                        val screen: GenericListSettingScreen = backStackEntry.toRoute()
//                        GenericListSettingScreen(screen, userStyle) {
//                            // TODO: implement
//                        }
//                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "EditorActivity"
    }
}