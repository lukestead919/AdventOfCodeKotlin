package aoc2024

import utils.Point
import utils.println
import utils.split

private data class Machine(
    val xTarget: Long,
    val yTarget: Long,
    val aVector: Point,
    val bVector: Point,
) {
    fun trySolve(): Long {
        // aPresses * aVector.x + bPresses * bVector.x = target.x
        // aPresses * aVector.y + bPresses * bVector.y = target.y

        // ( a.x  b.x ) ( aPresses ) = ( target.x )
        // ( a.y  b.y ) ( bPresses ) = ( target.y )

        // ( aPresses ) = 1/                 ( b.y  -b.x ) ( target.x )
        // ( bPresses ) = a.x*b.y - b.x*a.y  ( -a.y  a.x ) ( target.y )

        val discriminant = aVector.x * bVector.y - bVector.x * aVector.y
        if (discriminant == 0) return 0

        val a1 = bVector.y * xTarget - bVector.x * yTarget
        val b1 = aVector.x * yTarget - aVector.y * xTarget

        if (a1 % discriminant != 0L || b1 % discriminant != 0L) return 0

        val aPresses = a1 / discriminant
        val bPresses = b1 / discriminant

        return 3 * aPresses + bPresses
    }
}

fun main() {
    val machines =
        read2024Input("13")
            .split { it == "" }
            .map { (aString, bString, targetString) ->
                val regex = Regex(""".*?(\d+), .*?(\d+).*""")
                fun parsePair(s: String): Pair<Int, Int> =
                    regex.find(s)!!.destructured.let { (x, y) -> x.toInt() to y.toInt() }

                fun parsePoint(s: String): Point = parsePair(s).let { (x, y) -> Point(x, y) }

                Machine(
                    aVector = parsePoint(aString),
                    bVector = parsePoint(bString),
                    xTarget = parsePair(targetString).first.toLong(),
                    yTarget = parsePair(targetString).second.toLong(),
                )
            }

    fun part1() {
        machines.sumOf { it.trySolve() }.println()
    }

    fun part2() {
        machines
            .map {
                it.copy(
                    xTarget = it.xTarget + 10000000000000,
                    yTarget = it.yTarget + 10000000000000,
                )
            }
            .sumOf { it.trySolve() }
            .println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
