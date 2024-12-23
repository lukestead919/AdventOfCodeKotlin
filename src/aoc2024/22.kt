package aoc2024

import utils.combinations
import utils.iterate
import utils.println

fun Long.mix(other: Long): Long = this xor other

fun Long.prune(): Long = Math.floorMod(this, 16777216L)

fun Long.nextSecret(): Long =
    let { mix(64 * this).prune() }.let { it.mix(it / 32).prune() }.let { it.mix(it * 2048).prune() }

fun main() {
    val secrets = read2024Input("22").map { it.toLong() }

    fun part1() {
        secrets.sumOf { it.iterate(2000) { it.nextSecret() } }.println()
    }

    fun part2() {
        val secretMaps =
            secrets.map { startingSecret ->
                (1..2000)
                    .asSequence()
                    .runningFold(startingSecret) { previousSecret, _ ->
                        previousSecret.nextSecret()
                    }
                    .map { (it % 10).toInt() }
                    .zipWithNext()
                    .map { (a, b) -> b - a to b }
                    .windowed(4)
                    .toList()
                    .reversed()
                    .associate { window -> window.map { it.first } to window.last().second }
            }

        val sequences =
            (-9..9)
                .toList()
                .combinations(4)
                .filter { it.sum() in 0..9 }
                .filter { it.last() in 1..9 }

        sequences.maxOf { sequence -> secretMaps.sumOf { it.getOrDefault(sequence, 0) } }.println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
