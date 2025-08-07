package nodomain.pacjo.wear.watchface.feature.cell_grid

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

typealias Grid2d<T> = Array<Array<T>>

/**
 * Indexing helper to skip manual decomposition
 */
operator fun <T> Grid2d<T>.get(position: Vector2d): T {
    return this[position.y][position.x]
}

/**
 * Indexing helper to skip manual decomposition
 */
operator fun <T> Grid2d<T>.set(position: Vector2d, value: T) {
    this[position.y][position.x] = value
}

fun <T> Grid2d<T>.isInBounds(position: Vector2d): Boolean {
    val gridWidth = this[0].size
    val gridHeight = this.size

    return (position.y in 0..<gridHeight) && (position.x in 0..<gridWidth)
}

fun <T> Grid2d<T>.forEachPosition(
    action: (Vector2d) -> Unit
) {
    this.forEachIndexed { y, row ->
        row.forEachIndexed { x, cell ->
            action(Vector2d(x, y))
        }
    }
}

fun <T> Grid2d<T>.setBorder(borderCellValue: T) {
    val gridWidth = this[0].size
    val gridHeight = this.size

    val gridCenter = Vector2d(gridWidth / 2, gridHeight / 2)
    val radius = min(gridWidth / 2, gridHeight / 2)       // TODO: for rectangular devices we should consider cornerRadius instead

    // basically we set every cell outside of the circle as BORDER
    this.forEachPosition { position ->
        val distance = sqrt(
            abs(position.x - gridCenter.x).toFloat().pow(2) +
                    abs(position.y - gridCenter.y).toFloat().pow(2)
        )
        if (distance >= radius)
            this[position] = borderCellValue
    }
}