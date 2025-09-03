package nodomain.pacjo.wear.watchface.feature.cell_grid

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.annotation.ColorInt

data class GridSpec(
    val width: Float,
    val height: Float,
    val horizontalSpacing: Float,
    val verticalSpacing: Float
) {
    companion object {
        fun fromBounds(bounds: Rect, cellsHorizontal: Int, cellsVertical: Int): GridSpec {
            val width = bounds.width().toFloat()
            val height = bounds.height().toFloat()
            val horizontalSpacing = width / cellsHorizontal
            val verticalSpacing = height / cellsVertical

            val spec = GridSpec(
                width,
                height,
                horizontalSpacing,
                verticalSpacing
            )

            return spec
        }

        fun fromBounds(bounds: Rect, size: Int): GridSpec =
            fromBounds(bounds, size, size)
    }
}

fun <T> Canvas.drawGridLines(
    grid: Grid2d<T>,
    gridSpec: GridSpec,
    @ColorInt color: Int,
    strokeWidth: Float = 0f
) {
    val linePaint = Paint().apply {
        this.color = color
        this.strokeWidth = strokeWidth
    }

    // horizontal
    for (lineNumH in 1..grid.width) {
        val lineLevel = lineNumH * gridSpec.horizontalSpacing
        drawLine(lineLevel, 0f, lineLevel, gridSpec.height, linePaint)
    }

    // vertical
    for (lineNumV in 1..grid.height) {
        val lineLevel = lineNumV * gridSpec.verticalSpacing
        drawLine(0f, lineLevel, gridSpec.width, lineLevel, linePaint)
    }
}

fun Canvas.drawCell(
    cellLocation: Vector2d,
    horizontalSpacing: Float,
    verticalSpacing: Float,
    paint: Paint
) {
    this.drawRect(
        horizontalSpacing * cellLocation.x,
        verticalSpacing * cellLocation.y,
        horizontalSpacing * (cellLocation.x + 1),
        verticalSpacing * (cellLocation.y + 1),
        paint
    )
}