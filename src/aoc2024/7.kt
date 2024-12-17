package aoc2024

import utils.combinations
import utils.println

enum class Operator {
    ADD,
    MUL,
    CONCAT;

    fun applyOperator(a: Long, b: Long): Long {
        return when (this) {
            ADD -> a + b
            MUL -> a * b
            CONCAT -> "$a$b".toLong()
        }
    }
}

data class Equation(val left: Long, val right: List<Long>) {

    fun canBeSolvedUsing(operators: List<Operator>): Boolean {
        val operationCombinations = operators.combinations(right.size - 1)

        return operationCombinations.any { operations ->
            right.reduceIndexed { index, acc, nextNum ->
                val operator = operations[index - 1]
                val result = operator.applyOperator(acc, nextNum)
                result
            } == left
        }
    }
}

fun main() {
    val input =
        read2024Input("7").map {
            it.split(": ").let { (a, b) -> Equation(a.toLong(), b.split(" ").map { it.toLong() }) }
        }

    fun getSumOfValidEquationsUsingOperators(operators: List<Operator>): Long {
        return input.filter { it.canBeSolvedUsing(operators) }.sumOf { it.left }
    }

    fun part1() {
        getSumOfValidEquationsUsingOperators(listOf(Operator.ADD, Operator.MUL)).println()
    }

    fun part2() {
        getSumOfValidEquationsUsingOperators(listOf(Operator.ADD, Operator.MUL, Operator.CONCAT))
            .println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
