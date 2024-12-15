package aoc2024

import utils.println

fun main() {
  fun readInput(): Pair<List<Pair<Int, Int>>, List<List<Int>>> {
    val (rules, updates) =
        read2024Input("5").filter { it.isNotBlank() }.partition { it.contains("|") }

    val rulesParsed =
        rules.map {
          val (a, b) = it.split("|").map { it.toInt() }
          a to b
        }

    val updatesParsed = updates.map { it.split(",").map { it.toInt() } }
    return rulesParsed to updatesParsed
  }

  val (rules, updates) = readInput()

  fun updateObeysRule(update: List<Int>, rule: Pair<Int, Int>): Boolean {
    val (a, b) = rule
    val indexOfA = update.indexOf(a)
    val indexOfB = update.indexOf(b)
    if (indexOfA == -1 || indexOfB == -1) {
      return true
    }
    return indexOfA < indexOfB
  }

  fun midpoint(list: List<Int>) = list.get(list.size / 2)

  fun part1() {
    updates
        .filter { update -> rules.all { rule -> updateObeysRule(update, rule) } }
        .map { midpoint(it) }
        .sum()
        .println()
  }

  fun iterativelyFix(update: List<Int>): List<Int> {
    for (rule in rules) {
      if (!updateObeysRule(update, rule)) {
        val (a, b) = rule
        val indexOfA = update.indexOf(a)
        val indexOfB = update.indexOf(b)
        val temp = update[indexOfA]
        val mutUpdate = update.toMutableList()
        mutUpdate[indexOfA] = update[indexOfB]
        mutUpdate[indexOfB] = temp

        return iterativelyFix(mutUpdate.toList())
      }
    }
    return update
  }

  fun part2() {
    updates
        .filter { update -> rules.any { rule -> !updateObeysRule(update, rule) } }
        .map { iterativelyFix(it) }
        .sumOf { midpoint(it) }
        .println()
  }

  print("Part 1: ")
  part1()
  print("Part 2: ")
  part2()
}
