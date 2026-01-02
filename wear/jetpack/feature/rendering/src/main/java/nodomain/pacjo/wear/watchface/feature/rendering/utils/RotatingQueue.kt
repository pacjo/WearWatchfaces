package nodomain.pacjo.wear.watchface.feature.rendering.utils

class RotatingQueue<T>(
    val maxSize: Int
) {
    val deque = ArrayDeque<T>(maxSize)

    fun add(element: T) {
        if (deque.size == maxSize) {
            deque.removeLast()
        }

        deque.addFirst(element)
    }
}