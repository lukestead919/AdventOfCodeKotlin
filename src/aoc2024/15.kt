package aoc2024

import utils.*

private fun fromChar(char: Char): Direction {
    return when (char) {
        '^' -> Direction.UP
        '>' -> Direction.RIGHT
        'v' -> Direction.DOWN
        '<' -> Direction.LEFT
        else -> error("Invalid direction")
    }
}

private enum class Item {
    WALL,
    EMPTY,
    BOX,
    BOX_LEFT,
    BOX_RIGHT,
    ROBOT,
}

private sealed class MoveResult

private data object HitWall : MoveResult()

private data class MovedBoxTo(val boxMovedTo: Point) : MoveResult()

private data class MovedWithChanges(val changes: Set<Point>) : MoveResult()

fun main() {

    fun part1() {
        val (grid, instructions) =
            read2024Input("15")
                .split { it == "" }
                .let { (gridString, instructionsString) ->
                    val grid =
                        gridString
                            .asGrid()
                            .mapValues { (_, value) ->
                                when (value) {
                                    "#" -> Item.WALL
                                    "." -> Item.EMPTY
                                    "O" -> Item.BOX
                                    "@" -> Item.ROBOT
                                    else -> throw IllegalArgumentException("Invalid item")
                                }
                            }
                            .let(::Grid)
                    val instructions =
                        instructionsString.joinToString(separator = "").map(::fromChar)
                    grid to instructions
                }

        val endGrid =
            instructions.fold(grid) { currentGrid, direction ->
                val robotPosition = currentGrid.entries.find { it.value == Item.ROBOT }!!.key

                fun getMove(): MoveResult {
                    var nextPosition = robotPosition
                    while (true) {
                        nextPosition = nextPosition.move(direction)
                        val item = currentGrid[nextPosition]
                        when (item) {
                            Item.WALL -> return HitWall
                            Item.EMPTY -> return MovedBoxTo(nextPosition)
                            Item.BOX -> {}
                            else -> throw Error("Invalid item")
                        }
                    }
                }

                when (val move = getMove()) {
                    is HitWall -> currentGrid
                    is MovedWithChanges -> throw Error("Not implemented")
                    is MovedBoxTo ->
                        currentGrid.copy(
                            grid =
                                currentGrid.grid +
                                    mapOf(
                                        robotPosition to Item.EMPTY,
                                        move.boxMovedTo to Item.BOX,
                                        robotPosition.move(direction) to Item.ROBOT,
                                    )
                        )
                }
            }

        endGrid.filter { it.value == Item.BOX }.keys.sumOf { it.x + 100 * it.y }.println()
    }

    fun part2() {
        val (grid, instructions) =
            read2024Input("15")
                .split { it == "" }
                .let { (gridString, instructionsString) ->
                    val grid =
                        gridString
                            .map {
                                it.flatMap {
                                    when (it) {
                                        '#' -> listOf(Item.WALL, Item.WALL)
                                        '.' -> listOf(Item.EMPTY, Item.EMPTY)
                                        'O' -> listOf(Item.BOX_LEFT, Item.BOX_RIGHT)
                                        '@' -> listOf(Item.ROBOT, Item.EMPTY)
                                        else -> throw IllegalArgumentException("Invalid item")
                                    }
                                }
                            }
                            .asGrid()
                    val instructions =
                        instructionsString.joinToString(separator = "").map(::fromChar)
                    grid to instructions
                }

        val endGrid =
            instructions.foldIndexed(grid) { index, currentGrid, direction ->
                val robotPosition = currentGrid.entries.find { it.value == Item.ROBOT }!!.key

                fun move(point: Point, direction: Direction): MoveResult {
                    val item = currentGrid.getValue(point)
                    val movingTo = point.move(direction)

                    fun moveSimpleItem(): MoveResult =
                        when (val moveOthers = move(movingTo, direction)) {
                            is HitWall -> HitWall
                            is MovedBoxTo -> throw Error("Not implemented")
                            is MovedWithChanges -> {
                                MovedWithChanges(moveOthers.changes + point)
                            }
                        }

                    fun moveBox(): MoveResult {
                        if (direction == Direction.RIGHT || direction == Direction.LEFT) {
                            return moveSimpleItem()
                        }

                        val isLeft = item == Item.BOX_LEFT

                        val connectedBoxPosition =
                            if (isLeft) {
                                point.move(Direction.RIGHT)
                            } else {
                                point.move(Direction.LEFT)
                            }
                        val connectedBoxMovingTo = connectedBoxPosition.move(direction)

                        val moveOthers = move(movingTo, direction)
                        val moveOthersFromConnectedBox = move(connectedBoxMovingTo, direction)

                        if (moveOthers is HitWall || moveOthersFromConnectedBox is HitWall) {
                            return HitWall
                        }

                        if (moveOthers is MovedBoxTo || moveOthersFromConnectedBox is MovedBoxTo) {
                            throw Error("Not implemented")
                        }

                        return MovedWithChanges(
                            (moveOthers as MovedWithChanges).changes +
                                (moveOthersFromConnectedBox as MovedWithChanges).changes +
                                setOf(point, connectedBoxPosition)
                        )
                    }

                    return when (item) {
                        Item.WALL -> return HitWall
                        Item.BOX -> throw Error("Not implemented")
                        Item.EMPTY -> MovedWithChanges(emptySet())
                        Item.ROBOT -> moveSimpleItem()
                        Item.BOX_LEFT,
                        Item.BOX_RIGHT -> moveBox()
                    }
                }

                when (val move = move(robotPosition, direction)) {
                    is HitWall -> currentGrid
                    is MovedBoxTo -> throw Error("Not implemented")
                    is MovedWithChanges -> {
                        val movements =
                            move.changes.associate {
                                it.move(direction) to currentGrid.getValue(it)
                            }
                        val newEmptySpaces = move.changes - movements.keys

                        currentGrid.copy(
                            grid =
                                currentGrid.grid +
                                    movements +
                                    newEmptySpaces.associateWith { Item.EMPTY }
                        )
                    }
                }
            }

        endGrid.filter { it.value == Item.BOX_LEFT }.keys.sumOf { it.x + 100 * it.y }.println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
