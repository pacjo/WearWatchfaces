package nodomain.pacjo.wear.watchface.feature.cell_grid

/**
 * Data class similar to [Pair] but with methods useful for some basic vector math.
 */
data class Vector2d(
    val x: Int,
    val y: Int
) {
    operator fun plus(value: Vector2d): Vector2d {
        return Vector2d(this.x + value.x, this.y + value.y)
    }

    operator fun minus(value: Vector2d): Vector2d {
        return Vector2d(this.x - value.x, this.y - value.y)
    }

    operator fun times(value: Int): Vector2d {
        return Vector2d(this.x * value, this.y * value)
    }
}