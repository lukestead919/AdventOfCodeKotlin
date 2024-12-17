package aoc2024

import utils.println

fun Long.nextStones(): List<Long> =
    when {
        this == 0L -> listOf(1L)
        ((this.toString().length % 2) == 0) -> {
            val stringRepresentation = this.toString()
            val halfLength = stringRepresentation.length / 2
            listOf(
                stringRepresentation.take(halfLength).toLong(),
                stringRepresentation.takeLast(halfLength).toLong(),
            )
        }
        else -> listOf(this * 2024)
    }

private val numberOfStonesCache = mutableMapOf<Pair<Long, Int>, Long>()

fun Long.countStonesAfter(generationsRemaining: Int): Long {
    if (generationsRemaining == 0) return 1

    return numberOfStonesCache.getOrPut(this to generationsRemaining) {
        nextStones().sumOf { it.countStonesAfter(generationsRemaining - 1) }
    }
}

fun main() {
    val input = read2024Input("11").single().split(" ").map { it.toLong() }

    fun part1() {
        input.sumOf { it.countStonesAfter(25) }.println()
    }

    fun part2() {
        input.sumOf { it.countStonesAfter(75) }.println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
