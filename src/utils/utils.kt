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

fun <T> List<T>.combinations(size: Int): List<List<T>> {
    if (size == 0) return listOf(emptyList())
    if (size == 1) return map { listOf(it) }
    return flatMap { t -> combinations(size - 1).map { it + t } }
}

fun <T> List<T>.permutations(size: Int): List<List<T>> {
    if (size == 0) return listOf(emptyList())
    return flatMap { t -> minusElement(t).permutations(size - 1).map { it + t } }
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

    operator fun plus(other: Point) = Point(x + other.x, y + other.y)

    operator fun minus(other: Point) = Point(x - other.x, y - other.y)

    operator fun times(scalar: Int) = Point(x * scalar, y * scalar)
}
