package nodomain.pacjo.wear.watchface.shared.utils

/**
 * Data class similar to [Pair] but with methods useful for some basic vector math.
 */
// this mess is a result of kotlin missing generic math on Number or similar class
sealed interface Vector2 {
    val x: Number
    val y: Number

    data class Vector2i(override val x: Int, override val y: Int) : Vector2 {
        operator fun plus(other: Vector2i) =
            Vector2i(this.x + other.x, this.y + other.y)

        operator fun minus(other: Vector2i) =
            Vector2i(this.x - other.x, this.y - other.y)

        operator fun times(value: Int) =
            Vector2i(this.x * value, this.y * value)
    }

    data class Vector2f(override val x: Float, override val y: Float) : Vector2 {
        operator fun plus(other: Vector2f) =
            Vector2f(this.x + other.x, this.y + other.y)

        operator fun minus(other: Vector2f) =
            Vector2f(this.x - other.x, this.y - other.y)

        operator fun times(value: Float) =
            Vector2f(this.x * value, this.y * value)
    }

    data class Vector2d(override val x: Double, override val y: Double) : Vector2 {
        operator fun plus(other: Vector2d) =
            Vector2d(this.x + other.x, this.y + other.y)

        operator fun minus(other: Vector2d) =
            Vector2d(this.x - other.x, this.y - other.y)

        operator fun times(value: Double) =
            Vector2d(this.x * value, this.y * value)
    }
}