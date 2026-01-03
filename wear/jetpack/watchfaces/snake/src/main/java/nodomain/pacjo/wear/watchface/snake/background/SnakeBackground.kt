package nodomain.pacjo.wear.watchface.snake.background

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import nodomain.pacjo.wear.watchface.shared.RenderingContext
import nodomain.pacjo.wear.watchface.feature.background.Background
import nodomain.pacjo.wear.watchface.feature.cell_grid.Grid2d
import nodomain.pacjo.wear.watchface.feature.cell_grid.GridSpec
import nodomain.pacjo.wear.watchface.shared.utils.Vector2.Vector2i
import nodomain.pacjo.wear.watchface.feature.cell_grid.drawCell
import nodomain.pacjo.wear.watchface.feature.cell_grid.setBorder
import nodomain.pacjo.wear.watchface.snake.R
import java.time.ZonedDateTime
import java.util.PriorityQueue
import kotlin.math.abs
import kotlin.random.Random

object SnakeBackground : Background() {
    override val id = "snake"
    override val displayNameResourceId = R.string.snake_background

    private var game: SnakeGame? = null

    // for updating every set time amount
    private var lastZonedDateTime = ZonedDateTime.now()

    override fun draw(renderingContext: RenderingContext) {
        renderingContext.ifCanvas { canvas, bounds, zonedDateTime ->
            // fill background
            canvas.drawColor(Color.BLACK)

            // create SnakeGame if we don't already have one
            if (game == null) {
                game = SnakeGame(bounds, 25)
            }

            // update it's bounds regardless, it'll do noting if bounds don't change
            game?.updateBounds(bounds)

            // update grid every 100 milliseconds and only when drawing,
            // so we don't update the game when watchface isn't visible
            if (lastZonedDateTime.plusNanos(100_000_000) < zonedDateTime) {
                game?.update()
                lastZonedDateTime = zonedDateTime
            }

            // finally draw
            game?.draw(canvas)
        }
        // TODO: support opengl
    }
}

class SnakeGame(
    initialBounds: Rect,
    val gridSize: Int
) {
    private var state = State.PLAYING

    private var grid = Grid2d<CellType>(gridSize) { CellType.NONE }
    private var gridSpec = GridSpec.fromBounds(initialBounds, gridSize)

    // format: [head, body, body, ..., body, tail]
    private var snake = mutableListOf<Vector2i>()
    private lateinit var snakeDirection: Vector2i

    private lateinit var food: Vector2i
    private var pathToFood: List<Vector2i>? = null

    // bound can change at runtime, so we keep track of them
    var currentBounds: Rect = Rect()

    init {
        updateBounds(initialBounds)

        if (gridSize % 2 == 0)
            Log.w(TAG, "Grid size even. $TAG works better with odd grid sizes")

        resetGame()
    }

    /**
     * Main game loop.
     */
    fun update() {
        when (state) {
            State.PLAYING -> {
                // TODO: emptyCellCount calculation used .sumOf before moving to Grid2d class,
                //  do something to clean this workaround up
                var emptyCellCount = 0
                grid.forEachPosition { position ->
                    if (grid[position] == CellType.NONE)
                        emptyCellCount++
                }
                if (emptyCellCount == snake.size) {
                    state = State.WON
                    Log.i(TAG, "YOU WON! Snake size: ${snake.size}")
                    return // finish early, skip moving snake
                }

                if (pathToFood == null) {
                    state = State.LOST
                    Log.i(TAG, "GAME OVER! No path to food.")
                    return // finish early, skip moving snake
                }

                // If all is well, move the snake
                moveSnake()
            }

            // reset otherwise
            State.WON, State.LOST -> {
                Log.i(TAG, "Game completed, resetting...")
                resetGame()
            }
        }
    }

    private fun resetGame() {
        Log.i(TAG, "Starting new game...")

        // clear old state
        snake.clear()
        state = State.PLAYING

        // setup grid border
        grid.setBorder(CellType.BORDER)

        // set starting snake and food position
        placeSnake()
        placeFood()

        // calculate initial path
        calculatePathToFood()
    }

    fun draw(canvas: Canvas) {
//        drawPath(canvas)
        drawSnake(canvas)
        drawFood(canvas)
        drawObstacles(canvas)
//        canvas.drawGridLines(canvas)
    }

    private fun drawObstacles(canvas: Canvas) {
        grid.forEachPosition { position ->
            val cell = grid[position]
            if (cell == CellType.OBSTACLE) {
                canvas.drawCell(
                    position,
                    gridSpec.horizontalSpacing,
                    gridSpec.verticalSpacing,
                    Paint().apply {
                        color = Color.WHITE
                    }
                )
            }
        }
    }

    private fun drawSnake(canvas: Canvas) {
        snake.forEach { snakePart ->
            canvas.drawCell(
                snakePart,
                gridSpec.horizontalSpacing,
                gridSpec.verticalSpacing,
                Paint().apply {
                    color = Color.GREEN
                }
            )
        }
    }

    private fun drawFood(canvas: Canvas) {
        // only draw if it exists
        canvas.drawCell(
            food,
            gridSpec.horizontalSpacing,
            gridSpec.verticalSpacing,
            Paint().apply {
                color = Color.RED
            }
        )
    }

    @Suppress("unused")     // might decide to use it someday
    private fun drawPath(canvas: Canvas) {
        pathToFood?.forEach { pathElement ->
            canvas.drawCell(
                pathElement,
                gridSpec.horizontalSpacing,
                gridSpec.verticalSpacing,
                Paint().apply {
                    color = Color.BLUE
                    alpha = 100
                }
            )
        }
    }

    private fun placeSnake() {
        snakeDirection = SnakeDirection.entries.random().direction

        // starting position
        val startingSnakeSize = 3
        val snakeHead = findEmptyPosition { headPosition ->
            // make sure we can fully extend the snake
            val tailPosition = headPosition - snakeDirection * startingSnakeSize

            grid.isInBounds(tailPosition) && grid[tailPosition] == CellType.NONE
        }
        snake.add(snakeHead)

        (1..startingSnakeSize).forEach { _ ->
            snake.add(snake.last() - snakeDirection)
        }

        Log.i(TAG, "Created snake: $snake with direction: $snakeDirection")
    }

    fun moveSnake() {
        // path includes the head's current position, so we want the next step.
        val nextStep = pathToFood!!.getOrNull(1) ?: return // a guard for the end of path

        // add next step as the new head
        snake.add(0, nextStep)

        // check if the snake ate the food
        if (nextStep == food) {
            // don't remove tail (snake grows), just spawn new food
            placeFood()
        } else {
            // remove tail
            snake.removeAt(snake.lastIndex)
        }

        // update path
        calculatePathToFood()
    }

    private fun placeFood() {
        food = findEmptyPosition()
        Log.i(TAG, "Placed food at: $food")

        // after spawning food, calculate the path to it
        calculatePathToFood()
    }

    private fun calculatePathToFood() {
        val snakeHead = snake.first()

        // Define a heuristic - Manhattan distance
        val heuristic = { node: Vector2i ->
            abs(node.x - food.x) + abs(node.y - food.y)
        }

        // Call your corrected aStar function!
        pathToFood = aStar(snakeHead, food, heuristic)
    }

    /**
     * Finds empty position on current [grid].
     * [extraCheck] lambda can be used for passing additional requirements to the empty position checker
     * @param extraCheck lambda, which should return true if position is NOT accepted
     */
    private fun findEmptyPosition(
        extraCheck: (Vector2i) -> Boolean = { true }
    ): Vector2i {
        var position: Vector2i
        do {
            val x = Random.nextInt(grid.width)
            val y = Random.nextInt(grid.height)
            position = Vector2i(x, y)
        }
        // keep looping until we find a position which is empty
        while (snake.contains(position) || grid[position] != CellType.NONE || !extraCheck(position))

        return position
    }

    /**
     * Recalculate all spacing and dimension properties based on new bounds.
     */
    fun updateBounds(newBounds: Rect) {
        // Only do the work if the bounds have actually changed.
        if (newBounds == currentBounds) return

        Log.i(TAG, "Bounds changed, recalculating...")
        currentBounds.set(newBounds)

        gridSpec = GridSpec.fromBounds(newBounds, gridSize)
    }

    // https://en.wikipedia.org/wiki/A*_search_algorithm
    fun aStar(
        start: Vector2i,
        goal: Vector2i,
        h: (Vector2i) -> Int        // cost function
    ): List<Vector2i>? {
        fun reconstructPath(cameFrom: MutableMap<Vector2i, Vector2i>, current: Vector2i): List<Vector2i> {
            var innerCurrent = current
            val fullPath = mutableListOf(current)
            while (innerCurrent in cameFrom.keys) {
                innerCurrent = cameFrom.getValue(innerCurrent)
                fullPath.add(0, innerCurrent)
            }
            return fullPath
        }

        val gScore = mutableMapOf<Vector2i, Int>()
        gScore[start] = 0

        val fScore = mutableMapOf<Vector2i, Int>()
        fScore[start] = h(start)

        val openSet = PriorityQueue<Vector2i>(compareBy { fScore.getOrDefault(it, Int.MAX_VALUE) })
        openSet.add(start)      // add starting point

        val cameFrom = mutableMapOf<Vector2i, Vector2i>()

        // exclude the snake's tail, as it will move out of the way
        val snakeBody = if (snake.size > 1) snake.dropLast(1).toSet() else emptySet()

        while (openSet.isNotEmpty()) {
            val current = openSet.poll()!!

            if (current == goal)
                return reconstructPath(cameFrom, current)

            val currentNeighbours = SnakeDirection.entries
                .map { allowedDirection -> current - allowedDirection.direction }
                // filter out neighbors that are off-grid
                .filter { it.x >= 0 && it.x < grid.width && it.y >= 0 && it.y < grid.height }
                // filter out neighbors that are obstacles
                .filter { grid[it] == CellType.NONE }
                // filter out parts of snake to avoid going over them
                .filter { !snakeBody.contains(it) }

            currentNeighbours.forEach { neighbour ->
                val tentativeGScore = gScore.getOrDefault(current, Int.MAX_VALUE) + 1       // 1 is the d(current, neighbor)
                if (tentativeGScore < gScore.getOrDefault(neighbour, Int.MAX_VALUE)) {
                    cameFrom[neighbour] = current
                    gScore[neighbour] = tentativeGScore
                    fScore[neighbour] = tentativeGScore + h(neighbour)

                    if (!openSet.contains(neighbour))
                        openSet.add(neighbour)
                }
            }
        }

        return null         // failure
    }

    companion object {
        const val TAG = "SnakeGame"

        private enum class SnakeDirection(val direction: Vector2i) {
            UP(Vector2i(0, -1)),
            DOWN(Vector2i(0, 1)),
            LEFT(Vector2i(-1, 0)),
            RIGHT(Vector2i(1, 0))
        }

        private enum class CellType {
            NONE,
            OBSTACLE,
            BORDER
        }

        enum class State {
            PLAYING,
            WON,
            LOST
        }
    }
}