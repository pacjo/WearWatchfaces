package nodomain.pacjo.wear.watchface.feature.background.backgrounds

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.Rect
import android.graphics.drawable.AnimatedImageDrawable
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale
import nodomain.pacjo.wear.watchface.feature.background.Background
import nodomain.pacjo.wear.watchface.feature.background.R

@RequiresApi(Build.VERSION_CODES.P)
class GifPlayerBackground(
    val context: Context,
    @DrawableRes backgroundDrawable: Int
) : Background() {
    override val id = "gif_$backgroundDrawable"
    override val displayNameResourceId = R.string.background_setting      // TODO: change!!!!!

    private val animatedDrawable: AnimatedImageDrawable

    init {
        val source = ImageDecoder.createSource(context.resources, backgroundDrawable)
        animatedDrawable = ImageDecoder.decodeDrawable(source) as AnimatedImageDrawable

        // start animation on init
        animatedDrawable.start()
    }

    override fun draw(canvas: Canvas, bounds: Rect) {
        // clear canvas before drawing
        canvas.drawColor(Color.BLACK)       // TODO: maybe make configurable?

        // I'd love to use AnimatedDrawable.draw(Canvas), but it doesn't respect set bounds
        // we extract the bitmap and scale it instead to work around the issue
        val bitmap = animatedDrawable.toBitmap()
        bitmap.scale(bounds.width(), bounds.height())
        canvas.drawBitmap(bitmap, null, bounds, null)
    }
}