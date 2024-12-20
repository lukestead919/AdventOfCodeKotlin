package aoc2024

import utils.Grid
import utils.Point
import utils.println

fun main() {
    val bits =
        read2024Input("18").map { it.split(",").map { it.toInt() }.let { (a, b) -> Point(a, b) } }

    fun dijkstra(grid: Grid<String>, start: Point, end: Point): Int? {
        val dijk: MutableMap<Point, Int> = mutableMapOf(start to 0)
        val visited = mutableSetOf<Point>()

        while (!dijk.containsKey(end) && dijk.size != visited.size) {
            val next = dijk.entries.filter { it.key !in visited }.minBy { it.value }
            val (point, value) = next
            visited.add(point)

            fun addPoint(point: Point, value: Int) {
                dijk.merge(point, value) { a, b -> minOf(a, b) }
            }

            point
                .orthogonalNeighbors()
                .filter { grid[it] != "#" }
                .filter { it.x in (0..70) && it.y in (0..70) }
                .forEach { addPoint(it, value + 1) }
        }

        return dijk[end]
    }

    fun part1() {
        val firstBits = bits.take(1024)
        val grid = Grid(71, 71) { point -> if (point in firstBits) "#" else "." }
        dijkstra(grid, Point(0, 0), Point(70, 70)).println()
    }

    fun part2() {
        for (i in 1024 until bits.size) {
            val firstBits = bits.take(i)
            val grid = Grid(71, 71) { point -> if (point in firstBits) "#" else "." }
            val result = dijkstra(grid, Point(0, 0), Point(70, 70))
            if (result == null) {
                print(firstBits.last())
                break
            }
        }
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
