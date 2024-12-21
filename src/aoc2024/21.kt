package aoc2024

import aoc2024.DirectionPadCode.*
import utils.*

private enum class DirectionPadCode {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    ENTER;

    fun asChar(): Char {
        return when (this) {
            UP -> '^'
            DOWN -> 'v'
            LEFT -> '<'
            RIGHT -> '>'
            ENTER -> 'A'
        }
    }
}

private fun Direction.toPadCode(): DirectionPadCode {
    return when (this) {
        Direction.UP -> UP
        Direction.DOWN -> DOWN
        Direction.LEFT -> LEFT
        Direction.RIGHT -> RIGHT
        else -> {
            error("Invalid direction: $this")
        }
    }
}

fun main() {
    val codes = read2024Input("21")

    val numpad =
        Grid(3, 4) {
            when (it) {
                Point(0, 0) -> '7'
                Point(1, 0) -> '8'
                Point(2, 0) -> '9'
                Point(0, 1) -> '4'
                Point(1, 1) -> '5'
                Point(2, 1) -> '6'
                Point(0, 2) -> '1'
                Point(1, 2) -> '2'
                Point(2, 2) -> '3'
                Point(0, 3) -> '!'
                Point(1, 3) -> '0'
                Point(2, 3) -> 'A'
                else -> error("Invalid point $it")
            }
        }

    val directionPad =
        Grid(3, 2) {
            when (it) {
                Point(0, 0) -> '!'
                Point(1, 0) -> '^'
                Point(2, 0) -> 'A'
                Point(0, 1) -> '<'
                Point(1, 1) -> 'v'
                Point(2, 1) -> '>'
                else -> error("Invalid point $it")
            }
        }
    val numpadReverse = numpad.entries.associate { (k, v) -> v to k }
    val directionPadReverse = directionPad.entries.associate { (k, v) -> v to k }

    val movesCache: MutableMap<Triple<Point, Point, Set<Point>>, List<List<Direction>>> =
        mutableMapOf()

    fun generateMoves(
        start: Point,
        end: Point,
        impassablePoints: Set<Point>,
    ): List<List<Direction>> =
        movesCache.getOrPut(Triple(start, end, impassablePoints)) {
            if (start == end) return listOf(emptyList())
            if (start in impassablePoints) return emptyList()

            fun movesFrom(next: Point): List<List<Direction>> {
                return generateMoves(next, end, impassablePoints)
            }

            val moves = mutableListOf<List<Direction>>()
            if (start.x < end.x) {
                val moveRight = List(end.x - start.x) { Direction.RIGHT }
                val next = start.copy(x = end.x)
                val nextMoves = movesFrom(next).map { moveRight + it }
                moves.addAll(nextMoves)
            }
            if (start.x > end.x) {
                val moveLeft = List(start.x - end.x) { Direction.LEFT }
                val next = start.copy(x = end.x)
                val nextMoves = movesFrom(next).map { moveLeft + it }
                moves.addAll(nextMoves)
            }
            if (start.y < end.y) {
                val moveDown = List(end.y - start.y) { Direction.DOWN }
                val next = start.copy(y = end.y)
                val nextMoves = movesFrom(next).map { moveDown + it }
                moves.addAll(nextMoves)
            }
            if (start.y > end.y) {
                val moveUp = List(start.y - end.y) { Direction.UP }
                val next = start.copy(y = end.y)
                val nextMoves = movesFrom(next).map { moveUp + it }
                moves.addAll(nextMoves)
            }

            return moves
        }

    val numpadMovesCache: MutableMap<Pair<Point, Point>, List<List<DirectionPadCode>>> =
        mutableMapOf()
    fun generateMovesForNumpad(start: Point, end: Point): List<List<DirectionPadCode>> =
        numpadMovesCache.getOrPut(start to end) {
            generateMoves(
                    start,
                    end,
                    numpad.entries.filter { it.value == '!' }.map { it.key }.toSet(),
                )
                .map { moves -> moves.map { it.toPadCode() } + ENTER }
        }

    val directionMovesCache: MutableMap<Pair<Point, Point>, List<List<DirectionPadCode>>> =
        mutableMapOf()
    fun generateMovesForDirectionPad(start: Point, end: Point): List<List<DirectionPadCode>> =
        directionMovesCache.getOrPut(start to end) {
            generateMoves(
                    start,
                    end,
                    directionPad.entries.filter { it.value == '!' }.map { it.key }.toSet(),
                )
                .map { moves -> moves.map { it.toPadCode() } + ENTER }
        }

    fun calculateDirectionsForCode(code: String): List<List<DirectionPadCode>> {
        val isNumberCode = code.contains(Regex("[0-9]"))
        val pad = if (isNumberCode) numpadReverse else directionPadReverse
        val generateMovesForPad =
            if (isNumberCode) ::generateMovesForNumpad else ::generateMovesForDirectionPad

        return ("A$code").zipWithNext().fold(listOf(emptyList())) {
            previousMoves,
            (startValue, endValue) ->
            val start = pad.getValue(startValue)
            val end = pad.getValue(endValue)
            val validMoves = generateMovesForPad(start, end)

            previousMoves.flatMap { previousMove -> validMoves.map { previousMove + it } }
        }
    }

    fun part1() {
        codes
            .map { startCode ->
                (0..2).fold(listOf(listOf(startCode))) { previousCodes, _ ->
                    previousCodes.flatMap { code ->
                        //                        code.println()
                        calculateDirectionsForCode(code.last()).map {
                            code + it.map { it.asChar() }.joinToString(separator = "")
                        }
                    }
                }
            }
            .map {
                val startingCode = it[0][0]
                val startingCodeValue = startingCode.dropLast(1).toInt()
                it.minOf { it.last().length } * startingCodeValue
            }
            .sum()
            .println()
    }

    fun part2() {
        // A directional code can be broken up into a series of moves starting from A, going to one
        // or two of the four directions and pressing them and then back to A
        // direction, and then going back to A
        val hmCountLength = hashMapOf<Pair<String, Int>, Long>()

        fun getEventualLengthOfCode(code: String, roundsRemaining: Int): Long =
            hmCountLength.getOrPut(code to roundsRemaining) {
                //                code.println()

                if (roundsRemaining == 0) {
                    return@getOrPut code.length.toLong()
                }

                calculateDirectionsForCode(code).minOf { possibleCode ->
                    possibleCode
                        .split { it == ENTER }
                        .dropLast(1)
                        .map {
                            (it + ENTER).joinToString(separator = "") { it.asChar().toString() }
                        }
                        .sumOf { getEventualLengthOfCode(it, roundsRemaining - 1) }
                }
            }

        codes
            .map { it to getEventualLengthOfCode(it, 26) }
            .sumOf { (code, length) ->
                val startingCodeValue = code.dropLast(1).toLong()
                length * startingCodeValue
            }
            .println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
