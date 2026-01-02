package nodomain.pacjo.wear.watchface.feature.cell_grid

import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

// extension since I don't feel like this should be a part of the class
fun <T> Grid2d<T>.setBorder(borderCellValue: T) {
    val gridCenter = Vector2d(this.width / 2, this.height / 2)
    val radius = min(this.width / 2, this.height / 2)       // TODO: for rectangular devices we should consider cornerRadius instead

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

class Grid2d<T>(
    val width: Int,
    val height: Int,
    init: (position: Vector2d) -> T
) {
    private val grid =
        MutableList(height) { y ->
            MutableList(width) { x ->
                init(Vector2d(x, y))
            }
        }

    constructor(size: Int, init: (position: Vector2d) -> T) : this(size, size, init)

    /**
     * Indexing helper to skip manual decomposition
     */
    operator fun get(position: Vector2d): T {
        return grid[position.y][position.x]
    }

    /**
     * Indexing helper to skip manual decomposition
     */
    operator fun set(position: Vector2d, value: T) {
        grid[position.y][position.x] = value
    }

    fun isInBounds(position: Vector2d): Boolean {
        return (position.y in 0..<height) && (position.x in 0..<width)
    }

    fun forEachPosition(
        action: (position: Vector2d) -> Unit
    ) {
        grid.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                action(Vector2d(x, y))
            }
        }
    }

    fun deepCopy(): Grid2d<T> {
        return Grid2d(width, height) { position ->
            this[position]
        }
    }

    fun contentDeepHashCode(): Int {
        return grid.hashCode()
    }
}