package aoc2024

import utils.Point
import utils.println
import utils.split

private data class Machine(val target: Point, val aVector: Point, val bVector: Point) {
    fun trySolve(): Int? =
        (0..100)
            .mapNotNull { aPresses ->
                val remainder = target - (aVector * aPresses)
                val bPressesGuess = remainder.x / bVector.x

                if (remainder == bVector * bPressesGuess) {
                    (3 * aPresses) + bPressesGuess
                } else {
                    null
                }
            }
            .minOrNull()
}

fun main() {
    val machines =
        read2024Input("13")
            .split { it == "" }
            .map { (aString, bString, targetString) ->
                val regex = Regex(""".*?(\d+), .*?(\d+).*""")
                fun parsePoint(s: String): Point =
                    regex.find(s)!!.destructured.let { (x, y) -> Point(x.toInt(), y.toInt()) }

                Machine(
                    aVector = parsePoint(aString),
                    bVector = parsePoint(bString),
                    target = parsePoint(targetString),
                )
            }

    fun part1() {
        machines.mapNotNull { it.trySolve() }.sum().println()
    }

    fun part2() {}

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
