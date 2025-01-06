package aoc2024

import utils.*

fun parse(key: Grid<String>): List<Int> =
    (0..(key.maxOf { it.key.x })).map { xVal -> key.count { it.key.x == xVal && it.value == "#" } }

fun main() {
    val (keys, locks) =
        read2024Input("25")
            .split { it == "" }
            .map { it.asGrid() }
            .partition { it[Point(0, 0)] == "." }
            .let { (keys, locks) -> keys.map { parse(it) } to locks.map { parse(it) } }

    fun fits(key: List<Int>, lock: List<Int>): Boolean = key.zip(lock).all { (k, l) -> k + l <= 7 }

    fun part1() {
        keys.sumOf { key -> locks.count { lock -> fits(key, lock) } }.println()
    }

    print("Part 1: ")
    part1()
}
