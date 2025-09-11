package nodomain.pacjo.wear.watchface.jimball.sound

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSetting
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import nodomain.pacjo.wear.watchface.feature.base.FeatureFactory
import nodomain.pacjo.wear.watchface.feature.base.WatchFaceFeature

class BackgroundMusicPlayerFeature(
    @RawRes private val soundResource: Int
) : FeatureFactory {
    override fun getStyleSettings(context: Context) = emptyList<UserStyleSetting>()

    override fun create(
        context: Context,
        coroutineScope: CoroutineScope,
        currentUserStyleRepository: CurrentUserStyleRepository,
        watchState: WatchState
    ): WatchFaceFeature {
        return BackgroundMusicPlayer(coroutineScope, watchState, context, soundResource)
    }
}

class BackgroundMusicPlayer(
    private val scope: CoroutineScope,
    private val watchState: WatchState,
    context: Context,
    @RawRes musicResourceId: Int
): WatchFaceFeature {
    private val mediaPlayer = MediaPlayer.create(context, musicResourceId)

    init {
        // set some options
        mediaPlayer.isLooping = true
        mediaPlayer.setVolume(0.5f, 0.5f)

        // observe watchState
        scope.launch {
            combine(watchState.isVisible, watchState.isAmbient) { isVisible, isAmbient ->
                isVisible == true && isAmbient == false
            }.collect { shouldPlaySound ->
                if (shouldPlaySound && !mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                } else if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            }
        }
    }
}