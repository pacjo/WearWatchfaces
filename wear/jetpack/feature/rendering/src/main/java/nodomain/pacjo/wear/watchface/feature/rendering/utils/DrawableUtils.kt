package nodomain.pacjo.wear.watchface.feature.rendering.utils

import android.content.Context
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.P)
fun decodeAnimatedDrawable(
    context: Context,
    @DrawableRes animatedDrawableResId: Int,
    startAnimation: Boolean = true
): AnimatedImageDrawable {
    val source = ImageDecoder.createSource(context.resources, animatedDrawableResId)
    val animatedDrawable = ImageDecoder.decodeDrawable(source) as AnimatedImageDrawable

    // conditionally start animation on init
    if (startAnimation)
        animatedDrawable.start()

    return animatedDrawable
}