package javabin.game

import javabin.*

object TicTacToePrinter {
    fun printBoard(board: TicTacToeBoard) {
        fun toSymbol(pos: Placement): String = when (pos) {
            Placement.Occupied(Player.One) -> "X"
            Placement.Occupied(Player.Two) -> "O"
            else -> "."
        }

        val size = board.boardValues.size
        for (i in 0 until size) {
            for (j in 0 until size) {
                print(toSymbol(board.boardValues[i][j]) + " ")
            }
            println()
        }
    }

    fun printStatus(status: Status) {
        when (status) {
            Status.Win(Player.One) -> println("Player 1 wins")
            Status.Win(Player.Two) -> println("Player 2 wins")
            Status.Draw -> println("Draw")
            is Status.InProgress -> println("In progress")
            else -> IllegalStateException("Not a valid status")
        }
    }
}
