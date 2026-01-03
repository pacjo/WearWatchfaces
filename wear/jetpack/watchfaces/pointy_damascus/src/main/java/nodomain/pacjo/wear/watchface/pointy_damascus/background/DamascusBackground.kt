package nodomain.pacjo.wear.watchface.pointy_damascus.background

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.util.Log
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nodomain.pacjo.wear.watchface.feature.background.Background
import nodomain.pacjo.wear.watchface.feature.cell_grid.Grid2d
import nodomain.pacjo.wear.watchface.shared.utils.Vector2.Vector2d
import nodomain.pacjo.wear.watchface.shared.utils.Vector2.Vector2i
import nodomain.pacjo.wear.watchface.shared.RenderingContext
import nodomain.pacjo.wear.watchface.pointy_damascus.R
import org.openrndr.extra.noise.simplex2D
import kotlin.time.measureTime

class DamascusBackground : Background() {
    override val id = "damascus"
    override val displayNameResourceId = R.string.damascus_background

    private var heightMap: HeightMap? = null

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, _ ->
            // clear
            canvas.drawColor(Color.BLACK)

            if (heightMap == null)
                heightMap = HeightMap(bounds, 0.005, 8, false)
            else
                heightMap?.updateBounds(bounds)

            heightMap?.drawContours(canvas)
        }
    }
}

// TODO: move to shared module when another watchface will require this
class HeightMap(
    initialBounds: Rect,
    val scale: Double,
    val pixelsPerPoint: Int,
    val shouldInterpolate: Boolean
) {
    private var currentBounds: Rect = Rect()

    // we calculate this on every bounds change in updateBounds
    // which also include the run during initialization
    private lateinit var grid: Grid2d<Double>
    private var isInitialized = false

    // we'll use this to cache paths when drawing contours
    private val pathCache = mutableMapOf<Double, Path>()

    init {
        updateBounds(initialBounds)
    }

    fun drawRaw(canvas: Canvas) {
        if (!isInitialized)
            return      // we're not ready, return early

        val width = grid.height
        val height = grid.width

        val bitmap = createBitmap(width, height)
        val pixels = IntArray(width * height)

        // we can draw interesting background by adding step in loops below
        // and drawing bitmap without stretching (skipping pixels instead)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixelColor = Color.argb(grid[Vector2i(x, y)].toFloat(), 1f, 1f, 1f)
                pixels[y * width + x] = pixelColor
            }
        }

        // Set all pixels at once
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        canvas.drawBitmap(bitmap, null, currentBounds, null)
    }

    // following: https://urbanspr1nter.github.io/marchingsquares/
    fun drawContours(canvas: Canvas) {
        fun Path.moveAndLine(from: Vector2d, to: Vector2d) {
            moveTo(from.x.toFloat(), from.y.toFloat())
            lineTo(to.x.toFloat(), to.y.toFloat())
        }

        fun getPathForIsovalue(isovalue: Double): Path {
            val path = Path()

            // set vertex value based on isovalue
            val tempGrid = Grid2d(grid.width, grid.height) { false }
            tempGrid.forEachPosition { position ->
                tempGrid[position] = grid[position] >= isovalue
            }

            // for each 2x2 square (where squares overlap)
            for (y in 0 until grid.height - 1) {
                for (x in 0 until grid.width - 1) {
                    val topLeft = Vector2i(x, y)
                    val topRight = Vector2i(x + 1, y)
                    val bottomRight = Vector2i(x + 1, y + 1)
                    val bottomLeft = Vector2i(x, y + 1)

                    fun getT(v1: Double, v2: Double): Double {
                        val range = v2 - v1

                        // don't divide by zero
                        if (range == 0.0)
                            return 0.0

                        return (isovalue - v1) / range
                    }

                    fun lerp(p1: Vector2i, p2: Vector2i, t: Double): Vector2d {
                        val x = p1.x * (1.0 - t) + p2.x * t
                        val y = p1.y * (1.0 - t) + p2.y * t

                        return Vector2d(x, y)
                    }

                    // standard algorithm without interpolation
                    val left: Vector2d; val top: Vector2d; val right: Vector2d; val bottom: Vector2d
                    if (shouldInterpolate) {
                        left = lerp(topLeft, bottomLeft, getT(grid[topLeft], grid[bottomLeft]))
                        top = lerp(topLeft, topRight, getT(grid[topLeft], grid[topRight]))
                        right = lerp(topRight, bottomRight, getT(grid[topRight], grid[bottomRight]))
                        bottom = lerp(bottomLeft, bottomRight, getT(grid[bottomLeft], grid[bottomRight]))
                    } else {
                        left = Vector2d(topLeft.x + 0.5, topLeft.y + 0.5) * pixelsPerPoint.toDouble()
                        top = Vector2d(topLeft.x + 0.5, topLeft.y + 0.5) * pixelsPerPoint.toDouble()
                        right = Vector2d(topRight.x + 0.5, topRight.y + 0.5) * pixelsPerPoint.toDouble()
                        bottom = Vector2d(bottomLeft.x + 0.5, bottomLeft.y + 0.5) * pixelsPerPoint.toDouble()
                    }

                    // not the best (or even good way, but will do)
                    var case = 0
                    if (tempGrid[topLeft])
                        case += 8
                    if (tempGrid[topRight])
                        case += 4
                    if (tempGrid[bottomRight])
                        case += 2
                    if (tempGrid[bottomLeft])
                        case += 1

                    when (case) {
                        0, 15 -> { /* no-op */ }
                        1, 14 -> { path.moveAndLine(left, bottom) }
                        2, 13 -> { path.moveAndLine(bottom, right) }
                        3, 12 -> { path.moveAndLine(left, right) }
                        4, 11 -> { path.moveAndLine(top, right) }
                        5 -> {
                            // top line
                            path.moveAndLine(left, top)
                            // bottom line
                            path.moveAndLine(bottom, right)
                        }
                        6, 9 -> { path.moveAndLine(top, bottom) }
                        7, 8 -> { path.moveAndLine(left, top) }
                        10 -> {
                            // top line
                            path.moveAndLine(left, bottom)
                            // bottom line
                            path.moveAndLine(top, right)
                        }

                        else -> throw IllegalStateException("Case $case outside of possible range: [0, 15]")
                    }
                }
            }

            return path
        }

        if (!isInitialized)
            return      // we're not ready, return early

        val isovalues = listOf(0.2, 0.35, 0.5, 0.65, 0.8)
        for (isovalue in isovalues) {
            val paint = Paint().apply {
                color = Color.WHITE
                alpha = ((isovalue + 0.2) * 255).toInt()

                // path specific
                style = Paint.Style.STROKE
                strokeWidth = 3f
            }

            canvas.drawPath(
                pathCache.getOrPut(isovalue) { getPathForIsovalue(isovalue) },
                paint
            )
        }
    }

    /**
     * Recalculate all spacing and dimension properties based on new bounds.
     */
    fun updateBounds(newBounds: Rect) {
        // Only do the work if the bounds have actually changed.
        if (newBounds == currentBounds) return

        Log.i(TAG, "Bounds changed, recalculating...")
        currentBounds.set(newBounds)

        grid = Grid2d(
            width = currentBounds.width() / pixelsPerPoint + 1,
            height = currentBounds.height() / pixelsPerPoint + 1
        ) { 0.0 }

        // TODO: consider removing coroutines
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch {
            val calculationTime = measureTime {
                calculateHeightMap()
                isInitialized = true
            }
            Log.d(TAG, "height map generation time: $calculationTime")
        }
    }

    private suspend fun calculateHeightMap() {
        val seed = 100

        withContext(Dispatchers.Default) {
            grid.forEachPosition { position ->
                val scaledPosition = position * pixelsPerPoint
                val height = simplex2D(seed, scaledPosition.x * scale, scaledPosition.y * scale)         // [-1.0, 1.0]
                val normalizedHeight = (height + 1.0) / 2.0                                              // [0.0, 1.0]
                grid[position] = normalizedHeight
            }

            // invalidate cache
            pathCache.clear()
        }
    }

    companion object {
        const val TAG = "HeightMap"
    }
}