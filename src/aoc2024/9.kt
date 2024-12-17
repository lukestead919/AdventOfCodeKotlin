package aoc2024

import utils.println
import utils.swap

fun <T> MutableList<T?>.defrag(): List<T?> {
    val toMove = indexOfLast { it != null }
    val toMoveTo = indexOfFirst { it == null }

    if (toMove < toMoveTo) {
        return this.toList()
    }

    swap(toMove, toMoveTo)
    return defrag()
}

data class Chunk(val size: Int, val value: Int?)

fun MutableList<Chunk>.defragChunks(fileIndex: Int): List<Chunk> {
    if (fileIndex == 0) {
        return this.toList()
    }

    val fileToMoveIndex = indexOfFirst { it.value == fileIndex }
    val fileToMove = get(fileToMoveIndex)

    val freeSpaceIndex = indexOfFirst { it.value == null && it.size >= fileToMove.size }

    if (freeSpaceIndex > -1 && (freeSpaceIndex < fileToMoveIndex)) {
        set(fileToMoveIndex, Chunk(fileToMove.size, null))

        fun combineEmptyChunksAtIndex(index: Int) {
            val chunk = getOrNull(index)
            val nextChunk = getOrNull(index + 1)
            if (
                chunk != null && chunk.value == null && nextChunk != null && nextChunk.value == null
            ) {
                set(index, Chunk(size = chunk.size + nextChunk.size, value = null))
                removeAt(index + 1)
            }
        }
        combineEmptyChunksAtIndex(fileToMoveIndex)
        combineEmptyChunksAtIndex(fileToMoveIndex - 1)

        val overwrittenFreeSpace = get(freeSpaceIndex)
        if (overwrittenFreeSpace.size == fileToMove.size) {
            removeAt(freeSpaceIndex)
        } else {
            set(freeSpaceIndex, Chunk(overwrittenFreeSpace.size - fileToMove.size, null))
        }
        add(freeSpaceIndex, fileToMove)
    }
    return defragChunks(fileIndex - 1)
}

fun List<Chunk>.expand(): List<Int> {
    return flatMap { chunk -> List(chunk.size) { chunk.value ?: 0 } }
}

fun main() {
    fun part1() {
        val input =
            read2024Input("9").single().flatMapIndexed { index, char ->
                val num = char.toString().toInt()
                val isFile = index % 2 == 0

                if (!isFile) {
                    List(num) { null }
                } else {
                    List(num) { index / 2 }
                }
            }

        input
            .toMutableList()
            .defrag()
            .filterNotNull()
            .foldIndexed(0L) { index, acc, value -> acc + (index * value) }
            .println()
    }

    fun part2() {
        val input =
            read2024Input("9").single().mapIndexed { index, char ->
                val num = char.toString().toInt()
                val isFile = index % 2 == 0

                if (!isFile) {
                    Chunk(num, null)
                } else {
                    Chunk(num, index / 2)
                }
            }
        input
            .toMutableList()
            .defragChunks(input.maxOf { it.value ?: 0 })
            .expand()
            .foldIndexed(0L) { index, acc, value -> acc + (index * value) }
            .println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
