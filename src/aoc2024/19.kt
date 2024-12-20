package aoc2024

import utils.println
import utils.split

fun main() {
    val (patterns, designs) =
        read2024Input("19")
            .split { it == "" }
            .let { (patterns, designs) -> patterns.single().split(", ") to designs }

    fun part1() {
        val regex = Regex("(${patterns.joinToString("|")})+").println()
        designs.count { it.matches(regex) }.println()
    }

    fun part2() {
        val hm = mutableMapOf<String, Long>()

        fun waysToMake(s: String): Long =
            hm.getOrPut(s) {
                if (s.isEmpty()) {
                    1
                } else
                    patterns.sumOf {
                        if (s.startsWith(it)) {
                            waysToMake(s.drop(it.length))
                        } else {
                            0
                        }
                    }
            }

        designs.sumOf { waysToMake(it) }.println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
