package aoc2024

import utils.Point
import utils.asGrid
import utils.println

fun main() {
    val grid = read2024Input("10").asGrid().mapValues { (_, value) -> value.toInt() }

    fun getTrailEnds(point: Point): List<Point> {
        val value = grid[point] ?: return emptyList()
        if (value == 9) return listOf(point)

        return point
            .orthogonalNeighbors()
            .filter { grid[it] == value + 1 }
            .map { getTrailEnds(it) }
            .fold(emptyList(), List<Point>::plus)
    }

    fun part1() {
        grid.entries
            .filter { (_, value) -> value == 0 }
            .sumOf { (point, _) -> getTrailEnds(point).toSet().size }
            .println()
    }

    fun part2() {
        grid.entries
            .filter { (_, value) -> value == 0 }
            .sumOf { (point, _) -> getTrailEnds(point).size }
            .println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
