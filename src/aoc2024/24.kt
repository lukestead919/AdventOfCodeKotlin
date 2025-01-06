package aoc2024

import kotlin.random.Random
import utils.println
import utils.split

private enum class Operation {
    AND,
    OR,
    XOR;

    companion object {
        fun fromString(string: String): Operation = valueOf(string.uppercase())
    }
}

private data class Gate(
    val input1: String,
    val operation: Operation,
    val input2: String,
    val output: String,
)

private fun List<Gate>.containsLoop(): Boolean {
    val hm = hashSetOf<String>()
    fun Gate.containsLoop(visited: Set<String>): Boolean {
        if (output in visited) return true
        if (output in hm) return false
        hm.add(output)

        return (find { it.output == input1 }?.containsLoop(visited + output) == true ||
            find { it.output == input2 }?.containsLoop(visited + output) == true)
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
                Operation.AND -> input1Value and input2Value
                Operation.OR -> input1Value or input2Value
                Operation.XOR -> input1Value xor input2Value
            }
        }

    fun solve(): Long =
        wires
            .filter { it.startsWith("z") }
            .sortedBy { it.drop(1).toInt() }
            .map { getWireValue(it) }
            .toLong()
}

private fun Boolean.toInt() = if (this) 1 else 0

private fun Int.toBoolean() = this == 1

private fun Iterable<Boolean>.toLong(): Long =
    reversed().joinToString(separator = "") { it.toInt().toString() }.toLong(2)

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
                                Gate(input1, Operation.fromString(operation), input2, output)
                            }
                    }
            }

    val wires = gates.flatMap { listOf(it.input1, it.input2, it.output) }.toSet()

    fun part1() {
        Solver(gates, initialState).solve().println()
    }

    fun part2() {
        fun findFirstBrokenOutput(gates: List<Gate>): Int =
            // pick 10 random inputs and find the first place where the output is wrong.
            (0..10).minOf {
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

                val expectedOutput = xInput.values.toLong() + yInput.values.toLong()

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

        fun getWireForNumber(char: Char, number: Int): String =
            "$char${number.toString().padStart(2, '0')}"

        fun swapOutputs(gates: List<Gate>, swaps: List<Pair<String, String>>): List<Gate> =
            gates.map { gate ->
                swaps.fold(gate) { acc, (gate1, gate2) ->
                    when (acc.output) {
                        gate1 -> acc.copy(output = gate2)
                        gate2 -> acc.copy(output = gate1)
                        else -> acc
                    }
                }
            }

        (0..<4)
            .fold(emptyList<Pair<String, String>>()) { previousSwaps, _ ->
                val newGates = swapOutputs(gates, previousSwaps)
                val dependents = wires.associateWith { getDependents(it, newGates) }

                val firstBrokenOutput = findFirstBrokenOutput(newGates)

                val firstBrokenOutputDependents =
                    dependents.getValue(getWireForNumber('z', firstBrokenOutput))
                val correctGates =
                    dependents.getOrDefault(
                        getWireForNumber('z', firstBrokenOutput - 1),
                        emptySet(),
                    )

                val brokenGateCandidates =
                    (firstBrokenOutputDependents - correctGates).filterNot {
                        it.startsWith("x") || it.startsWith("y")
                    } + getWireForNumber('z', firstBrokenOutput)

                val swapCandidates =
                    wires.filterNot { it.startsWith("x") || it.startsWith("y") } - correctGates

                val gateSwaps =
                    brokenGateCandidates.flatMap { brokenGateCandidate ->
                        swapCandidates.map { swapCandidate -> brokenGateCandidate to swapCandidate }
                    }

                gateSwaps
                    .associateWith { gateSwap -> swapOutputs(newGates, listOf(gateSwap)) }
                    .filterValues { !it.containsLoop() }
                    .maxBy { findFirstBrokenOutput(it.value) }
                    .let { previousSwaps + it.key }
            }
            .flatMap { it.toList() }
            .sorted()
            .joinToString(separator = ",")
            .println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
