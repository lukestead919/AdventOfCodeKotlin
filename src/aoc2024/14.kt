package aoc2024

import utils.Point
import utils.iterate
import utils.println

private data class Robot(val position: Point, val velocity: Point) {
    fun move() = copy(position = position + velocity)
}

fun main() {
    val robots =
        read2024Input("14").map { robotString ->
            val regex = Regex("""p=(-?\d+),(-?\d+) v=(-?\d+),(-?\d+)""")
            regex.find(robotString)!!.destructured.let { (x, y, vx, vy) ->
                Robot(Point(x.toInt(), y.toInt()), Point(vx.toInt(), vy.toInt()))
            }
        }

    val size = Point(robots.maxOf { it.position.x + 1 }, robots.maxOf { it.position.y + 1 })

    fun Robot.moveAndTeleport(): Robot {
        val next = move()
        return next.copy(
            position =
                Point(
                    Math.floorMod(next.position.x, size.x),
                    Math.floorMod(next.position.y, size.y),
                )
        )
    }

    fun part1() {
        robots
            .iterate(100) { it.map { robot -> robot.moveAndTeleport() } }
            .filter { it.position.x != size.x / 2 && it.position.y != size.y / 2 }
            .groupBy {
                when {
                    it.position.x < size.x / 2 && it.position.y < size.y / 2 -> 1
                    it.position.x < size.x / 2 && it.position.y > size.y / 2 -> 2
                    it.position.x > size.x / 2 && it.position.y < size.y / 2 -> 3
                    else -> 4
                }
            }
            .values
            .map { it.size }
            .reduce(Int::times)
            .println()
    }

    fun part2() {
        fun printRobots(robots: List<Robot>) {
            val grid = Array(size.y) { CharArray(size.x) { '.' } }
            robots.forEach { grid[it.position.y][it.position.x] = '#' }
            grid.forEach { println(it.joinToString("")) }
        }

        var step = 0
        robots.iterate(10000) {
            it.map { robot -> robot.moveAndTeleport() }
                .also {
                    step++
                    val totalPositionsTaken = it.map { it.position }.toSet()
                    val totalRobots = it.size
                    // Lucky guess :D
                    if (totalPositionsTaken.size == totalRobots) {
                        print("Step ${++step}\n")
                        printRobots(it)
                    }
                }
        }
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
