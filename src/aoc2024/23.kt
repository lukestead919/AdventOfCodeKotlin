package aoc2024

import utils.println

fun main() {
    val connections = read2024Input("23").map { it.split("-").let { (a, b) -> a to b } }

    val computers = connections.flatMap { (a, b) -> listOf(a, b) }.toSet()

    val graph: Map<String, Set<String>> =
        connections
            .flatMap { (a, b) -> listOf(a to b, b to a) }
            .groupBy({ it.first }, { it.second })
            .mapValues { it.value.toSet() }

    fun part1() {
        graph
            .filter { it.key[0] == 't' }
            .flatMap { (tComputer, tConnections) ->
                tConnections.flatMap { connectedComputer ->
                    graph[connectedComputer]!!.intersect(tConnections).map {
                        setOf(tComputer, connectedComputer, it)
                    }
                }
            }
            .toSet()
            .size
            .println()
    }

    fun part2() {
        var groupsOfSizeN = computers.flatMap { a -> graph[a]!!.map { b -> setOf(a, b) } }.toSet()

        while (true) {
            groupsOfSizeN =
                groupsOfSizeN
                    .flatMap { group ->
                        val intersection =
                            group.fold(computers) { acc, computer ->
                                acc.intersect(graph[computer]!!)
                            }

                        intersection.map { group + it }
                    }
                    .toSet()

            if (groupsOfSizeN.size <= 1) {
                groupsOfSizeN.first().toList().sorted().joinToString(",").println()
                break
            }
        }
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
