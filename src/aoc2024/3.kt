package aoc2024

import utils.println

fun main() {
    val input = read2024Input("3").joinToString { it }

    fun part1() {
        val regex = Regex("""mul\((\d+),(\d+)\)""")
        regex.findAll(input).sumOf {
            val (a, b) = it.destructured
            a.toInt() * b.toInt()
        }.println()
    }

    fun part2() {
        val doStarts = Regex("""do\(\)""").findAll(input).map { it.range.last }
        val doEnds = Regex("""don't\(\)""").findAll(input).map { it.range.last }

        val regex = Regex("""mul\((\d+),(\d+)\)""")
        regex.findAll(input).sumOf {
            val (a, b) = it.destructured
            val value = a.toInt() * b.toInt()

            val end = it.range.last
            val lastDoStart = doStarts.lastOrNull { it < end } ?: 0
            val lastDoEnd = doEnds.lastOrNull { it < end } ?: -1
            if (lastDoEnd > lastDoStart) {
                0
            } else {
                value
            }
        }.println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}