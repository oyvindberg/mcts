package javabin.game

import javabin.*

class TicTacToeBoard(internal val boardValues: Array<Array<Placement>> = boardofSize(DEFAULT_BOARD_SIZE), override val currentPlayer: Player, private val totalMoves: Int = 0) : Board {
    private fun emptyPositions(): List<Position> {
        val size = this.boardValues.size
        val emptyPositions = mutableListOf<Position>()
        for (i in 0 until size) {
            for (j in 0 until size) {
                if (boardValues[i][j] == Placement.Empty)
                    emptyPositions.add(Position(i, j))
            }
        }
        return emptyPositions
    }

    override fun withMove(player: Player, p: Position): TicTacToeBoard {
        val copy = TicTacToeBoard(Array(boardValues.size, { boardValues[it].copyOf() }), player, totalMoves + 1)
        copy.boardValues[p.x][p.y] = Placement.Occupied(player)
        return copy
    }

    override fun withPlayer(player: Player): Board {
        return TicTacToeBoard(boardValues, player, totalMoves)
    }

    override fun checkStatus(): Status {
        /**
         * First check the row for win,
         * builds an array of each column, then checks column for win.
         * In the end, build an array of each diagonal which is checked for win.
         */
        val boardSize = boardValues.size
        val maxIndex = boardSize - 1
        val diag1 = emptyPlacements(boardSize)
        val diag2 = emptyPlacements(boardSize)

        for (i in 0 until boardSize) {
            val row = boardValues[i]

            val checkRowForWin = checkForWin(row)
            if (checkRowForWin is Placement.Occupied)
                return Status.Win(checkRowForWin.player)

            val col = emptyPlacements(boardSize)
            for (j in 0 until boardSize) {
                col[j] = boardValues[j][i]
            }

            val checkColForWin = checkForWin(col)
            if (checkColForWin is Placement.Occupied)
                return Status.Win(checkColForWin.player)

            diag1[i] = boardValues[i][i]
            diag2[i] = boardValues[maxIndex - i][i]
        }

        val checkDia1gForWin = checkForWin(diag1)
        if (checkDia1gForWin is Placement.Occupied)
            return Status.Win(checkDia1gForWin.player)

        val checkDiag2ForWin = checkForWin(diag2)
        if (checkDiag2ForWin is Placement.Occupied)
            return Status.Win(checkDiag2ForWin.player)

        val positions = emptyPositions()
        return if (positions.isNotEmpty()) Status.InProgress(positions) else Status.Draw
    }

    private fun checkForWin(row: Array<Placement>): Placement {
        val first = row.first()
        return when (first) {
            is Placement.Occupied -> if (row.all { it == first }) first else Placement.Empty
            else -> Placement.Empty
        }
    }

    companion object {
        val DEFAULT_BOARD_SIZE = 3

        fun emptyPlacements(size: Int) = Array<Placement>(size, { Placement.Empty })

        private fun boardofSize(size: Int) = Array(size, { emptyPlacements(size) } )

        fun ofSize(size: Int): TicTacToeBoard {
            return TicTacToeBoard(boardofSize(size), Player.One)
        }
    }
}

sealed class Placement {
    object Empty : Placement()
    data class Occupied(val player: Player) : Placement()
}
