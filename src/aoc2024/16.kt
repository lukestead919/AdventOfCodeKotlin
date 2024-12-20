package aoc2024

import utils.Direction
import utils.Point
import utils.asGrid
import utils.println

fun main() {
    val input = read2024Input("16").asGrid()
    val startingPoint = input.entries.find { it.value == "S" }!!.key
    val endPoint = input.entries.find { it.value == "E" }!!.key

    fun part1() {
        val dijk: MutableMap<Pair<Point, Direction>, Int> =
            mutableMapOf((startingPoint to Direction.RIGHT) to 0)
        val visited = mutableSetOf<Pair<Point, Direction>>()

        while (
            !dijk.containsKey(endPoint to Direction.RIGHT) &&
                !dijk.containsKey(endPoint to Direction.UP)
        ) {
            val next = dijk.entries.filter { it.key !in visited }.minBy { it.value }
            val (position, value) = next
            val (point, direction) = position
            visited.add(position)
            val moveForward = point.move(direction)
            if (input.getValue(moveForward) != "#") {
                dijk[moveForward to direction] = value + 1
            }
            val left = direction.turnLeft()
            if (input.getValue(point.move(left)) != "#") {
                dijk[point to left] = value + 1000
            }
            val right = direction.turnRight()
            if (input.getValue(point.move(right)) != "#") {
                dijk[point to right] = value + 1000
            }
        }

        val answer = dijk[endPoint to Direction.RIGHT] ?: dijk[endPoint to Direction.UP]
        answer.println()
    }

    fun part2() {
        val target = 134588

        val dijk: MutableMap<Pair<Point, Direction>, Int> =
            mutableMapOf((startingPoint to Direction.RIGHT) to 0)
        val visited = mutableSetOf<Pair<Point, Direction>>()

        while (dijk.entries.filter { it.key !in visited }.minOf { it.value } < target) {
            fun updateDijk(point: Point, direction: Direction, value: Int) {
                dijk.merge(point to direction, value) { a, b -> minOf(a, b) }
            }

            val next = dijk.entries.filter { it.key !in visited }.minBy { it.value }
            val (position, value) = next
            val (point, direction) = position
            visited.add(position)
            val moveForward = point.move(direction)
            if (input.getValue(moveForward) != "#") {
                updateDijk(moveForward, direction, value + 1)
            }
            val left = direction.turnLeft()
            if (input.getValue(point.move(left)) != "#") {
                updateDijk(point, left, value + 1000)
            }
            val right = direction.turnRight()
            if (input.getValue(point.move(right)) != "#") {
                updateDijk(point, right, value + 1000)
            }
        }

        fun reverseSearch(point: Point, direction: Direction, pointsRemaining: Int): Set<Point> {
            val setOfValidPoints = mutableSetOf(point)

            val moveBackwards = point.move(direction.opposite())
            val valueBackwards = dijk[moveBackwards to direction]
            if (valueBackwards == pointsRemaining - 1) {
                setOfValidPoints.addAll(
                    reverseSearch(moveBackwards, direction, pointsRemaining - 1)
                )
            }

            val rotateLeft = direction.turnLeft()
            val valueLeft = dijk[point to rotateLeft]
            if (valueLeft == pointsRemaining - 1000) {
                setOfValidPoints.addAll(reverseSearch(point, rotateLeft, pointsRemaining - 1000))
            }

            val rotateRight = direction.turnRight()
            val valueRight = dijk[point to rotateRight]
            if (valueRight == pointsRemaining - 1000) {
                setOfValidPoints.addAll(reverseSearch(point, rotateRight, pointsRemaining - 1000))
            }

            return setOfValidPoints
        }

        (reverseSearch(endPoint, Direction.RIGHT, target) +
                reverseSearch(endPoint, Direction.UP, target))
            .size
            .println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
