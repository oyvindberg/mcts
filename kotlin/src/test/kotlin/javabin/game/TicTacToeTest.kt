package javabin.game

import javabin.Board
import javabin.mcts.MonteCarloTreeSearch
import javabin.Player
import javabin.Status
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.util.*


class TicTacToeTest {
    @Test
    fun givenEmptyBoard_whenSimulateInterAIPlay_thenGameDraw() {
        givenEmptyBoard_whenSimulateInterAIPlay_thenGameDraw(TicTacToeBoard.DEFAULT_BOARD_SIZE)
        givenEmptyBoard_whenSimulateInterAIPlay_thenGameDraw(4)
        givenEmptyBoard_whenSimulateInterAIPlay_thenGameDraw(6)
    }

    fun givenEmptyBoard_whenSimulateInterAIPlay_thenGameDraw(size: Int) {
        var board: Board = TicTacToeBoard.ofSize(size)
        var player: Player = Player.One
        val totalMoves = size * size
        val random = Random()
        val duration = Duration.ofMillis(500)

        for (i in 0 until totalMoves) {
            board = MonteCarloTreeSearch.findNextMove(board, player, random, duration)
            if (board.checkStatus() !is Status.InProgress) {
                break
            }
            player = player.opponent
        }
        val winStatus = board.checkStatus()
        TicTacToePrinter.printStatus(winStatus)
        TicTacToePrinter.printBoard(board as TicTacToeBoard)

        assertEquals(Status.Draw, winStatus)
    }

    @Test
    fun favorWinningMove() {
        val board = TicTacToeBoard(arrayOf(
                arrayOf(Placement.Occupied(Player.Two), Placement.Occupied(Player.Two), Placement.Empty),
                arrayOf(Placement.Occupied(Player.One), Placement.Occupied(Player.One), Placement.Empty),
                arrayOf(Placement.Occupied(Player.Two), Placement.Occupied(Player.Two), Placement.Empty)
        ), Player.One)
        val movedBoard = MonteCarloTreeSearch.findNextMove(board, Player.One, Random(0), Duration.ofMillis(500))

        val checkStatus = movedBoard.checkStatus()
        assertEquals(Status.Win(Player.One), checkStatus)
        TicTacToePrinter.printStatus(checkStatus)
        TicTacToePrinter.printBoard(movedBoard as TicTacToeBoard)
    }
}
