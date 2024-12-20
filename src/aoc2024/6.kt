package aoc2024

import utils.Direction
import utils.Point
import utils.println

enum class Piece(val char: Char) {
    EMPTY('.'),
    VISITED('x'),
    WALL('#'),
    PLAYER('@');

    companion object {
        fun fromChar(char: Char): Piece {
            return when (char) {
                EMPTY.char -> EMPTY
                VISITED.char -> VISITED
                WALL.char -> WALL
                PLAYER.char,
                '<',
                '^',
                '>',
                'v' -> PLAYER
                else -> throw IllegalArgumentException("Invalid char: $char")
            }
        }
    }
}

enum class EndReason {
    LOOP,
    OUT_OF_BOUNDS,
}

fun Char.toDirection(): Direction {
    return when (this) {
        '^' -> Direction.UP
        'v' -> Direction.DOWN
        '<' -> Direction.LEFT
        '>' -> Direction.RIGHT
        else -> throw IllegalArgumentException("Invalid char: $this")
    }
}

typealias Board = Map<Point, Piece>

fun Board.playerPosition(): Point? {
    return entries.find { it.value == Piece.PLAYER }?.key
}

private data class State(
    val board: Board,
    val playerPosition: Point?,
    val direction: Direction,
    val history: Set<Pair<Point, Direction>>,
) {
    fun advance(): State {
        val playerPosition = playerPosition ?: throw Exception("Player not found")
        val nextPosition = playerPosition.move(direction)
        val nextPiece = board[nextPosition]

        when (nextPiece) {
            null -> {
                return copy(
                    playerPosition = null,
                    history = history + (playerPosition to direction),
                )
            }
            Piece.WALL -> {
                return copy(
                    direction = direction.turnRight(),
                    history = history + (playerPosition to direction),
                )
            }
            else -> {
                return copy(
                    playerPosition = nextPosition,
                    history = history + (playerPosition to direction),
                )
            }
        }
    }

    fun advanceUntilEnd(): Pair<State, EndReason> {
        val nextState = advance()

        if (nextState.playerPosition == null) {
            return nextState to EndReason.OUT_OF_BOUNDS
        } else if (nextState.history.contains(nextState.playerPosition to nextState.direction)) {
            return nextState to EndReason.LOOP
        }

        return nextState.advanceUntilEnd()
    }
}

fun main() {
    val input: Lazy<State> = lazy {
        val input = read2024Input("6")
        val board =
            input
                .flatMapIndexed { y, row ->
                    row.mapIndexed { x, c -> Point(x, y) to Piece.fromChar(c) }
                }
                .associate { it }

        val playerPosition = board.playerPosition() ?: throw Exception("Player not found")
        val (x, y) = playerPosition
        val direction = input[y][x].toDirection()

        State(board, playerPosition, direction, emptySet())
    }

    fun part1() {
        val endState = input.value.advanceUntilEnd().first
        endState.history.map { it.first }.toSet().size.println()
    }

    fun part2() {
        val endState = input.value.advanceUntilEnd().first

        val possibleObstaclePositions =
            endState.history.map { it.first }.toSet() - input.value.playerPosition!!

        possibleObstaclePositions
            .count { obstaclePosition ->
                val modifiedState =
                    input.value.copy(board = input.value.board + (obstaclePosition to Piece.WALL))
                modifiedState.advanceUntilEnd().second == EndReason.LOOP
            }
            .println()
    }

    print("Part 1: ")
    part1()
    print("Part 2: ")
    part2()
}
