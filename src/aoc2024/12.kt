package aoc2024

import utils.Direction
import utils.Point
import utils.asGrid
import utils.println

fun Set<Point>.perimeter(): Int = sumOf { point ->
    val neighbors = point.orthogonalNeighbors()
    neighbors.count { it !in this }
}

fun Set<Point>.sides(): Int =
    listOf(Direction.DOWN, Direction.LEFT, Direction.RIGHT, Direction.UP).sumOf { direction ->
        // For each of the four directions, first find all external neighbours in that direction
        // then count any neighbours that are next to each other perpendicular to the direction to
        // be equivalent
        val neighboursInDirection = map { it.move(direction) }.filter { it !in this }

        val collapseDirection = direction.turnRight()
        val collapsedNeighbours =
            neighboursInDirection.filter { it.move(collapseDirection) !in neighboursInDirection }

        collapsedNeighbours.size
    }

fun main() {
    val grid = read2024Input("12").asGrid()

    fun buildGroup(
        start: Point,
        group: MutableSet<Point> = mutableSetOf(start),
    ): MutableSet<Point> {
        val neighbors = grid.orthogonalNeighbors(start)
        val plotValue = grid.getValue(start)
        neighbors.forEach {
            if (grid.getValue(it) == plotValue && it !in group) {
                group.add(it)
                buildGroup(it, group)
            }
        }
        return group
    }

    fun buildGroups(): List<Set<Point>> {
        val groups = mutableListOf<Set<Point>>()

        while (true) {
            val alreadyGrouped = groups.flatten().toSet()
            val notYetGrouped = grid.filter { it.key !in alreadyGrouped }
            if (notYetGrouped.isEmpty()) break

            val point = notYetGrouped.keys.first()
            val nextGroup = buildGroup(point)
            groups.add(nextGroup)
        }

        return groups
    }
    val groups = buildGroups()

    fun part1() {
        groups.sumOf { it.perimeter() * it.size }.println()
    }

    fun part2() {
        groups.sumOf { it.sides() * it.size }.println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
