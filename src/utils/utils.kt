package utils

import kotlin.io.path.Path
import kotlin.io.path.readText

fun readInput(year: String, name: String) =
    Path("src/aoc$year/DataFiles/$name.txt").readText().trim().lines()

fun <T : Any?> T.println(): T = also { println(this) }

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = mutableListOf<MutableList<T>>()
    for (i in this[0].indices) {
        val row = mutableListOf<T>()
        for (j in this.indices) {
            row.add(this[j][i])
        }
        result.add(row)
    }
    return result
}

fun <T> List<T>.split(predicate: (T) -> Boolean): List<List<T>> {
    val result = mutableListOf<MutableList<T>>()
    var currentList = mutableListOf<T>()
    for (element in this) {
        if (predicate(element)) {
            result.add(currentList)
            currentList = mutableListOf()
        } else {
            currentList.add(element)
        }
    }
    result.add(currentList)
    return result
}

fun List<String>.asGrid(
    splitRow: (String) -> List<String> = { it.map { it.toString() } }
): Grid<String> {
    return flatMapIndexed { y, row -> splitRow(row).mapIndexed { x, char -> Point(x, y) to char } }
        .associate { it }
        .let(::Grid)
}

fun <T> List<List<T>>.asGrid(): Grid<T> {
    return flatMapIndexed { y, row -> row.mapIndexed { x, char -> Point(x, y) to char } }
        .associate { it }
        .let(::Grid)
}

fun <T> List<T>.combinations(size: Int): List<List<T>> {
    if (size == 0) return listOf(emptyList())
    if (size == 1) return map { listOf(it) }
    return flatMap { t -> combinations(size - 1).map { it + t } }
}

fun <T> List<T>.permutations(size: Int): List<List<T>> {
    if (size == 0) return listOf(emptyList())
    return flatMap { t -> minusElement(t).permutations(size - 1).map { it + t } }
}

fun <T> MutableList<T>.swap(index1: Int, index2: Int) {
    val tmp = this[index1]
    this[index1] = this[index2]
    this[index2] = tmp
}

fun <T> T.iterate(times: Int, action: (T) -> T): T = (1..times).fold(this) { acc, _ -> action(acc) }

fun <T> T.iterateUntilStable(action: (T) -> T): T {
    var current = this
    var next = action(current)
    while (current != next) {
        current = next
        next = action(current)
    }
    return current
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    UP_LEFT,
    UP_RIGHT,
    DOWN_LEFT,
    DOWN_RIGHT;

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
            UP_LEFT -> DOWN_RIGHT
            UP_RIGHT -> DOWN_LEFT
            DOWN_LEFT -> UP_RIGHT
            DOWN_RIGHT -> UP_LEFT
        }
    }

    fun turnRight(): Direction {
        return when (this) {
            UP -> RIGHT
            DOWN -> LEFT
            LEFT -> UP
            RIGHT -> DOWN
            UP_LEFT -> UP_RIGHT
            UP_RIGHT -> DOWN_RIGHT
            DOWN_LEFT -> UP_LEFT
            DOWN_RIGHT -> DOWN_LEFT
        }
    }

    fun turnLeft(): Direction {
        return when (this) {
            UP -> LEFT
            DOWN -> RIGHT
            LEFT -> DOWN
            RIGHT -> UP
            UP_LEFT -> DOWN_LEFT
            UP_RIGHT -> UP_LEFT
            DOWN_LEFT -> DOWN_RIGHT
            DOWN_RIGHT -> UP_RIGHT
        }
    }
}

data class Point(val x: Int, val y: Int) {
    fun move(direction: Direction): Point {
        return when (direction) {
            Direction.UP -> copy(y = y - 1)
            Direction.DOWN -> copy(y = y + 1)
            Direction.LEFT -> copy(x = x - 1)
            Direction.RIGHT -> copy(x = x + 1)
            Direction.UP_LEFT -> copy(x = x - 1, y = y - 1)
            Direction.UP_RIGHT -> copy(x = x + 1, y = y - 1)
            Direction.DOWN_LEFT -> copy(x = x - 1, y = y + 1)
            Direction.DOWN_RIGHT -> copy(x = x + 1, y = y + 1)
        }
    }

    fun orthogonalNeighbors(): List<Point> =
        listOf(
            move(Direction.UP),
            move(Direction.RIGHT),
            move(Direction.DOWN),
            move(Direction.LEFT),
        )

    operator fun plus(other: Point) = Point(x + other.x, y + other.y)

    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    operator fun times(scalar: Int) = Point(x * scalar, y * scalar)
}

data class Grid<T>(val grid: Map<Point, T>) : Map<Point, T> by grid {
    constructor(
        x: Int,
        y: Int,
        valueGetter: (Point) -> T,
    ) : this(
        (0..<x)
            .flatMap { x -> (0..<y).map { y -> Point(x, y) to valueGetter(Point(x, y)) } }
            .toMap()
    )

    val points: Set<Point> = grid.keys

    fun print(display: (T) -> String = { it.toString() }) {
        val xRange = points.map { it.x }.let { it.minOrNull()!!..it.maxOrNull()!! }
        val yRange = points.map { it.y }.let { it.minOrNull()!!..it.maxOrNull()!! }
        for (y in yRange) {
            for (x in xRange) {
                print(get(Point(x, y))?.let { display(it) } ?: " ")
            }
            kotlin.io.println()
        }
    }

    fun orthogonalNeighbors(point: Point): List<Point> =
        point.orthogonalNeighbors().filter { it in points }
}
