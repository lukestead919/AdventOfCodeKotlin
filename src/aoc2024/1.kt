package aoc2024

import kotlin.math.abs
import utils.println
import utils.transpose

fun main() {
    val input =
        read2024Input("1").map {
            it.trim().split(" ").filter { it.isNotBlank() }.map { it.toInt() }
        }

    val (list1, list2) = input.transpose()

    fun part1() {
        list1.sorted().zip(list2.sorted()).sumOf { (a, b) -> abs(a - b) }.println()
    }

    fun part2() {
        val list2Counts = list2.groupingBy { it }.eachCount()
        list1.sumOf { it * list2Counts.getOrDefault(it, 0) }.println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
