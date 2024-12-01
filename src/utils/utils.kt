package utils

import kotlin.io.path.Path
import kotlin.io.path.readText

fun readInput(year: String, name: String) = Path("src/aoc$year/DataFiles/$name.txt").readText().trim().lines()

fun <T : Any?> T.println(): T = also { println(this) }

fun <T> List<List<T>>.transpose(): List<List<T>> {
    val result = mutableListOf<MutableList<T>>()
    for (i in this[0].indices) {
        val row = mutableListOf<T>()
        for (j in this.indices) {
            row.add(this[j][i])
        }
        result.add(row)
    }
    return result
}