package aoc2024

import utils.Direction
import utils.Point
import utils.println

fun main() {
    val input = read2024Input("4")

    fun getAt(p: Point): Char? {
        return input.getOrNull(p.y)?.getOrNull(p.x)
    }

    fun checkForWord(point: Point, direction: Direction, word: String): Boolean {
        var p = point.copy()
        for (char in word) {
            val charAtPosition = getAt(p)
            if (charAtPosition != char) {
                return false
            }
            p = p.move(direction)
        }
        return true
    }


    fun part1() {
        input.mapIndexed { y, row ->
            row.mapIndexed { x, _ ->
                Direction.entries.count {
                    checkForWord(Point(x, y), it, "XMAS")
                }
            }.sum()
        }.sum().println()
    }

    fun part2() {
        val directions = listOf(
            Direction.UP_LEFT,
            Direction.UP_RIGHT,
            Direction.DOWN_RIGHT,
            Direction.DOWN_LEFT,
        )
        val adjacentDirections = directions.zipWithNext() + (directions.last() to directions.first())

        fun isTheAInMas(point: Point, mDirection: Direction): Boolean
            = checkForWord(point.move(mDirection), mDirection.opposite(), "MAS")


        input.mapIndexed { y, row ->
            row.mapIndexed { x, _ ->
                adjacentDirections.count { (direction1, direction2) ->
                    isTheAInMas(Point(x, y), direction1) && isTheAInMas(Point(x, y), direction2)
                }
            }.sum()
        }.sum().println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}