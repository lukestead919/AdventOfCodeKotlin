package aoc2024

import utils.println
import kotlin.math.abs

fun main() {
    val reports = read2024Input("2").map {
        it.trim().split(" ").map { it.toInt() }
    }

    fun isSafeReport(report: List<Int>): Boolean {
        val isIncreasing = report.last() >= report.first()
        val isValid = report.zipWithNext().all { (a, b) ->
            val diff = b - a
            1 <= abs(diff) && abs(diff) <= 3  && diff >= 0 == isIncreasing
        }
        return isValid
    }

    fun part1() {
        reports.count { isSafeReport(it) }.println()
    }

    fun isAlmostSafeReport(report: List<Int>): Boolean {
        val reportsWithMissing = List(report.size) { i ->
            val mutableReport = report.toMutableList()
            mutableReport.removeAt(i)
            mutableReport
        }
        return reportsWithMissing.any { isSafeReport(it) }
    }

    fun part2() {
        reports.count { isSafeReport(it) || isAlmostSafeReport(it)}.println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}