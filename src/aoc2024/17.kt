package aoc2024

import kotlin.math.pow
import utils.iterateUntilStable
import utils.println

private data class Program(
    val a: Long,
    val b: Long,
    val c: Long,
    val program: List<Int>,
    val pointer: Int,
    val output: List<Int>,
) {
    fun literalOperand(): Int = program[pointer + 1]

    fun comboOperand(): Long =
        when (val operand = program[pointer + 1]) {
            0,
            1,
            2,
            3 -> operand.toLong()
            4 -> a
            5 -> b
            6 -> c
            else -> throw IllegalArgumentException("Invalid operand $operand")
        }

    fun advance(): Program {
        val opcode = program.getOrNull(pointer) ?: return this

        fun divideFunction(): Long = a / (2.0.pow(comboOperand().toDouble())).toInt()

        return when (opcode) {
            0 -> copy(a = divideFunction(), pointer = pointer + 2)
            1 -> copy(b = b xor literalOperand().toLong(), pointer = pointer + 2)
            2 -> copy(b = comboOperand() % 8, pointer = pointer + 2)
            3 -> if (a == 0L) copy(pointer = pointer + 2) else copy(pointer = literalOperand())
            4 -> copy(b = b xor c, pointer = pointer + 2)
            5 -> copy(output = output + (comboOperand() % 8).toInt(), pointer = pointer + 2)
            6 -> copy(b = divideFunction(), pointer = pointer + 2)
            7 -> copy(c = divideFunction(), pointer = pointer + 2)
            else -> throw IllegalArgumentException("Invalid opcode $opcode")
        }
    }

    fun reverse(): List<Program> {
        //        val opcode = program.getOrNull(pointer) ?: return this
        //
        //        fun multiplyFunction(): Long = a * (2.0.pow(comboOperand().toDouble())).toInt()
        //
        //        return when (opcode) {
        //            0 -> copy(a = multiplyFunction(), pointer = pointer + 2)
        //            1 -> copy(b = b xor literalOperand().toLong(), pointer = pointer + 2)
        //            2 -> copy(b = comboOperand() % 8, pointer = pointer + 2)
        //            3 -> if (a == 0L) copy(pointer = pointer + 2) else copy(pointer =
        // literalOperand())
        //            4 -> copy(b = b xor c, pointer = pointer + 2)
        //            5 -> {
        //                val nextOutput = output.dropLast(1)
        //                if (
        //                    quine &&
        //                        (nextOutput.size > program.size ||
        //                            program.take(nextOutput.size) != nextOutput)
        //                ) {
        //                    return copy(output = nextOutput, pointer = Int.MAX_VALUE)
        //                }
        //
        //                copy(output = nextOutput, pointer = pointer + 2)
        //            }
        //            6 -> copy(b = multiplyFunction(), pointer = pointer + 2)
        //            7 -> copy(c = multiplyFunction(), pointer = pointer + 2)
        //            else -> throw IllegalArgumentException("Invalid opcode $opcode")
        //        }

        // I feel like this should work, no idea why it doesn't
        when (pointer) {
            0 -> {
                return if (output.isEmpty()) {
                    listOf(this)
                } else {
                    listOf(copy(pointer = 14))
                }
            }
            2 -> {
                return if (b == (a % 8)) {
                    listOf(copy(pointer = 0, b = output.lastOrNull()?.toLong() ?: 0))
                } else {
                    emptyList()
                }
            }
            4 -> {
                return listOf(copy(pointer = 2, b = b xor 3))
            }
            6 -> {
                val validC = c == (a / 2.0.pow(b.toDouble()).toInt())
                if (!validC) {
                    return emptyList()
                }

                return (0L..<256).map { remainder -> copy(pointer = 4, c = remainder) }
            }
            8 -> {
                return listOf(copy(pointer = 6, b = b xor c))
            }
            10 -> {
                return (0..7).map { copy(pointer = 8, a = (8 * a) + it) }
            }
            12 -> {
                return listOf(copy(pointer = 10, b = b xor 5))
            }
            14 -> {
                if (b != output.last().toLong()) {
                    throw Error("Shouldn't happen $output, $b")
                }
                return listOf(copy(pointer = 12, output = output.dropLast(1)))
            }
            else -> {
                throw Error("Invalid pointer $pointer")
            }
        }
    }
}

fun main() {
    val program =
        read2024Input("17").let { (aStr, bStr, cStr, _, programStr) ->
            val a = aStr.removePrefix("Register A: ").toLong()
            val b = bStr.removePrefix("Register B: ").toLong()
            val c = cStr.removePrefix("Register C: ").toLong()

            val program = programStr.removePrefix("Program: ").split(",").map { it.toInt() }

            Program(a, b, c, program, 0, emptyList())
        }

    fun part1() {
        program.iterateUntilStable { it.advance() }.output.joinToString(separator = ",").println()
    }

    fun part2() {
        //        program
        //            .copy(a = 0, pointer = 14, output = program.program)
        //            .let { program -> (0L..<256).map { program.copy(c = it) } }
        //            .iterate(29) { it.flatMap { it.reverse() } }

        fun findA(previousA: Long, output: List<Int>): List<Long> =
            (0..7)
                .filter {
                    val a = (8 * previousA) + it
                    val newProgram = program.copy(a = a)
                    newProgram.iterateUntilStable { it.advance() }.output == output
                }
                .println()
                .map { 8L * previousA + it }

        (1..program.program.size)
            .foldIndexed(listOf(0L)) { index, previousA, _ ->
                val output = program.program.takeLast(index + 1)
                val possibleAs = previousA.flatMap { findA(it, output) }
                possibleAs
            }
            .min()
            .println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
