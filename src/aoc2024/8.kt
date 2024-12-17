package aoc2024

import utils.Point
import utils.permutations
import utils.println

fun main() {
    val input =
        read2024Input("8").flatMapIndexed { y, row ->
            row.mapIndexed { x, char -> Point(x, y) to char }
        }

    val validPoints = input.map { it.first }
    val frequencies = input.map { it.second }.toSet().minus('.')
    val antennaPairs =
        frequencies.flatMap { frequency ->
            val antennae = input.filter { it.second == frequency }.map { it.first }
            antennae.permutations(2)
        }

    fun part1() {
        antennaPairs
            .map { (node1, node2) -> (node1 + ((node2 - node1) * 2)) }
            .filter { it in validPoints }
            .toSet()
            .size
            .println()
    }

    fun part2() {
        antennaPairs
            .flatMap { (antenna1, antenna2) ->
                val antinodes = mutableListOf<Point>()
                val distanceVector = antenna2 - antenna1
                var node = antenna2
                do {
                    antinodes.add(node)
                    node += distanceVector
                } while (node in validPoints)
                antinodes
            }
            .toSet()
            .size
            .println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
