package aoc2024

import java.util.*
import utils.Grid
import utils.Point
import utils.asGrid
import utils.println

fun main() {
    val grid = read2024Input("20").asGrid()

    fun dijkstra(grid: Grid<String>, start: Point, end: Point): Int? {
        val dist: MutableMap<Point, Int> = mutableMapOf(start to 0)
        val points = grid.keys.toMutableSet()
        val queue = PriorityQueue<Pair<Point, Int>>(points.size, compareBy { it.second })
        queue.add(start to 0)

        while (queue.isNotEmpty()) {
            val (next, distance) = queue.poll()

            if (next == end) {
                break
            }
            points.remove(next)

            fun addPoint(point: Point, value: Int) {
                val existing = dist[point]
                if (existing == null) {
                    dist[point] = value
                    queue.add(point to value)
                    return
                }

                if (value >= existing) {
                    return
                }

                dist[point] = value
                queue.remove(point to existing)
                queue.add(point to value)
            }

            next
                .orthogonalNeighbors()
                .filter { grid[it] != "#" }
                .forEach { addPoint(it, distance + 1) }
        }

        return dist[end]
    }

    fun part1() {
        val start = grid.entries.find { it.value == "S" }!!.key
        val end = grid.entries.find { it.value == "E" }!!.key
        val noCheatTime = dijkstra(grid, start, end)!!

        val walls = grid.entries.filter { it.value == "#" }.map { it.key }

        walls
            .filter { grid.orthogonalNeighbors(it).count { grid[it] != "#" } >= 2 }
            .map {
                val newGrid = grid.toMutableMap() + (it to ".")
                noCheatTime - dijkstra(Grid(newGrid), start, end)!!
            }
            .filter { it >= 100 }
            .size
            .println()
    }

    fun part2() {
        fun getDistancesFrom(grid: Grid<String>, start: Point): Map<Point, Int> {
            val dist: MutableMap<Point, Int> = mutableMapOf(start to 0)
            val points = grid.keys.toMutableSet()
            val queue = PriorityQueue<Pair<Point, Int>>(points.size, compareBy { it.second })
            queue.add(start to 0)

            while (queue.isNotEmpty()) {
                val (next, distance) = queue.poll()
                points.remove(next)

                fun addPoint(point: Point, value: Int) {
                    val existing = dist[point]
                    if (existing == null) {
                        dist[point] = value
                        queue.add(point to value)
                        return
                    }

                    if (value >= existing) {
                        return
                    }

                    dist[point] = value
                    queue.remove(point to existing)
                    queue.add(point to value)
                }

                next
                    .orthogonalNeighbors()
                    .filter { grid[it] != "#" }
                    .forEach { addPoint(it, distance + 1) }
            }

            return dist
        }

        val start = grid.entries.find { it.value == "S" }!!.key
        val end = grid.entries.find { it.value == "E" }!!.key

        val noCheatTime = dijkstra(grid, start, end)!!

        val distancesFromStart = getDistancesFrom(grid, start)
        val distancesFromEnd = getDistancesFrom(grid, end)

        val emptySpaces = grid.keys.filter { grid[it] != "#" }.toSet()
        val validTeleports =
            emptySpaces.sumOf { teleportStart ->
                emptySpaces.count { teleportEnd ->
                    val manhattanDistance = teleportStart.manhattanDistance(teleportEnd)
                    fun newTime() =
                        distancesFromStart[teleportStart]!! +
                            manhattanDistance +
                            distancesFromEnd[teleportEnd]!!

                    teleportStart != teleportEnd &&
                        manhattanDistance <= 20 &&
                        newTime() <= noCheatTime - 100
                }
            }
        validTeleports.println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
