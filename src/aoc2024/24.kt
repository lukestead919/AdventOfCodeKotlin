package aoc2024

import kotlin.random.Random
import utils.iterateUntilStable
import utils.println
import utils.split

private data class Gate(
    val input1: String,
    val operation: String,
    val input2: String,
    val output: String,
)

private fun List<Gate>.containsLoop(): Boolean {
    fun Gate.containsLoop(visited: Set<String>): Boolean {
        if (output in visited) return true

        return find { it.output == input1 }?.containsLoop(visited + output) == true ||
            find { it.output == input2 }?.containsLoop(visited + output) == true
    }

    return any { it.containsLoop(emptySet()) }
}

private class Solver(private val gates: List<Gate>, initialWireValues: Map<String, Boolean>) {
    val wireValues = initialWireValues.toMutableMap()
    val wires by lazy { gates.flatMap { listOf(it.input1, it.input2, it.output) }.toSet() }

    fun getWireValue(wire: String): Boolean =
        wireValues.getOrPut(wire) {
            val gate = gates.single { it.output == wire }
            val input1Value = getWireValue(gate.input1)
            val input2Value = getWireValue(gate.input2)
            when (gate.operation) {
                "AND" -> input1Value and input2Value
                "OR" -> input1Value or input2Value
                "XOR" -> input1Value xor input2Value
                else -> throw Error("Invalid operation ${gate.operation}")
            }
        }

    fun solve(): Long =
        wires
            .filter { it.startsWith("z") }
            .sortedBy { it.drop(1).toInt() }
            .reversed()
            .map { getWireValue(it) }
            .joinToString("") { it.toInt().toString() }
            .toLong(2)
}

private fun Boolean.toInt() = if (this) 1 else 0

private fun Int.toBoolean() = this == 1

fun main() {
    val (initialState, gates) =
        read2024Input("24")
            .split { it == "" }
            .let { (initialState, gates) ->
                val letterNumberRegex = "(\\S+)"

                initialState
                    .map { it.split(": ") }
                    .associate { (a, b) -> a to b.toInt().toBoolean() } to
                    gates.map {
                        Regex(
                                "$letterNumberRegex $letterNumberRegex $letterNumberRegex -> $letterNumberRegex"
                            )
                            .matchEntire(it)!!
                            .destructured
                            .let { (input1, operation, input2, output) ->
                                Gate(input1, operation, input2, output)
                            }
                    }
            }

    val wires = gates.flatMap { listOf(it.input1, it.input2, it.output) }.toSet()

    fun part1() {
        Solver(gates, initialState).solve().println()
    }

    fun findFirstBrokenOutput(gates: List<Gate>): Int =
        (0..100).minOf {
            val xInput =
                wires
                    .filter { it.startsWith("x") }
                    .associateWith { Random.nextBoolean() }
                    .toSortedMap()
            val yInput =
                wires
                    .filter { it.startsWith("y") }
                    .associateWith { Random.nextBoolean() }
                    .toSortedMap()
            val expectedOutput =
                xInput.values
                    .reversed()
                    .joinToString(separator = "") { it.toInt().toString() }
                    .toLong(2) +
                    yInput.values
                        .reversed()
                        .joinToString(separator = "") { it.toInt().toString() }
                        .toLong(2)

            val output = Solver(gates, xInput + yInput).solve()

            val firstBrokenOutput =
                (output xor expectedOutput).toString(2).reversed().indexOfFirst { it == '1' }
            if (firstBrokenOutput == -1) {
                Int.MAX_VALUE
            } else {
                firstBrokenOutput
            }
        }

    fun getDependents(wire: String, gates: List<Gate>): Set<String> {
        if (wire in initialState.keys) return emptySet()
        val gate = gates.single { it.output == wire }
        return setOf(gate.input1, gate.input2) +
            getDependents(gate.input1, gates) +
            getDependents(gate.input2, gates)
    }

    fun part2() {
        gates.iterateUntilStable { newGates ->
            val dependents = wires.associateWith { getDependents(it, newGates) }

            val firstBrokenOutput = findFirstBrokenOutput(newGates)
            if (firstBrokenOutput == Int.MAX_VALUE) {
                return@iterateUntilStable newGates
            }

            val firstBrokenOutputDependents =
                dependents.getValue("z${firstBrokenOutput.toString().padStart(2, '0')}")
            val correctGates =
                dependents.getOrDefault(
                    "z${(firstBrokenOutput - 1).toString().padStart(2, '0')}",
                    emptySet(),
                )

            val brokenGateCandidates =
                (firstBrokenOutputDependents - correctGates).filterNot {
                    it.startsWith("x") || it.startsWith("y")
                } + "z${firstBrokenOutput.toString().padStart(2, '0')}"

            val swapCandidate =
                wires.filterNot { it.startsWith("x") || it.startsWith("y") } - correctGates

            val gateSwaps =
                brokenGateCandidates.flatMap { brokenGateCandidate ->
                    swapCandidate.map { swapCandidate -> brokenGateCandidate to swapCandidate }
                }

            gateSwaps
                .associateWith { (gate1, gate2) ->
                    newGates.map { gate ->
                        when (gate.output) {
                            gate1 -> gate.copy(output = gate2)
                            gate2 -> gate.copy(output = gate1)
                            else -> gate
                        }
                    }
                }
                .filterValues { !it.containsLoop() }
                .maxBy { findFirstBrokenOutput(it.value) }
                .also { println(it.key) }
                .value
        }
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
