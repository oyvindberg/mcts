package mcts.games

import mcts._
import mcts.cli.Runner
import mcts.games.Array2d.Index
import mcts.Player.{One, Two}
import mcts.solver.{MonteCarlo, Node}
import org.scalatest.{FunSuite, Matchers}

class TicTacToeTest extends FunSuite with Matchers {
  val Game = TicTacToe()

  test("should always favor the winning move in a game of Tic Tac Toe") {
    val actions = 0.until(10).map { _ =>
      // format: off
        val board =
          Board(
            Occupied(Two), Occupied(Two), Empty,
            Occupied(One), Occupied(One), Empty,
            Occupied(Two), Occupied(Two), Empty
          )
        // format: on
      val initialNode =
        Node.fromState[TicTacToeState, Index](
          Game.startingState.copy(board = board)
        )

      Runner(maxIterations = 1000)(initialNode)(
        node => MonteCarlo(Game).select(node, One)
      ).bestAction._1
    }

    actions.toSet should equal(Set(5))
  }

  test("should always block the winning move in a game of Tic Tac Toe") {
    val actions = 0.until(10).map { _ =>
      val board =
        // format: off
          Board(
            Empty, Empty, Occupied(Two),
            Empty, Occupied(Two), Empty,
            Empty, Empty, Empty
          )
        // format: on
      val initialNode =
        Node.fromState[TicTacToeState, Index](
          Game.startingState.copy(board = board)
        )
      Runner(maxIterations = 1000)(initialNode)(
        node => MonteCarlo(Game).select(node, One)
      ).bestAction._1
    }

    actions.toSet should equal(Set(6))
  }

  test("should recognize red as winner in") {
    // format: off
    val board = Board(
      Occupied(One),  Occupied(One),  Occupied(One),
      Occupied(Two), Occupied(Two), Occupied(One),
      Occupied(One),  Occupied(Two), Occupied(One)
    )
    // format: on

    val lines: Array2d[Array[Line]] =
      VictoryTemplate.linesForBoard(VictoryTemplate.ThreeInRow, board)

    WinnerCheck(lines(8), board) match {
      case Some((One, _)) => //ok
      case other          => fail(other.toString)
    }
  }

  test("should recognize blue as winner in") {
    // format: off
    val board = Board(
      Occupied(Two), Occupied(One),  Occupied(Two),
      Occupied(Two), Occupied(Two), Occupied(One),
      Occupied(Two), Occupied(One),  Occupied(One)
    )
    // format: on

    val lines: Array2d[Array[Line]] =
      VictoryTemplate.linesForBoard(VictoryTemplate.ThreeInRow, board)

    WinnerCheck(lines(0), board) match {
      case Some((Two, _)) => //ok
      case other          => fail(other.toString)
    }
  }

  test("should recognize draw") {
    // format: off
    val grid = Board(
      Occupied(Two), Occupied(One), Occupied(Two),
      Occupied(One), Occupied(Two), Occupied(One),
      Occupied(One), Occupied(Two), Occupied(One)
    )
    // format: on
    assert(Game.gameResult(Game.startingState.copy(board = grid)) == Draw)
  }

  def Board(s0: Placement,
            s1: Placement,
            s2: Placement,
            s3: Placement,
            s4: Placement,
            s5: Placement,
            s6: Placement,
            s7: Placement,
            s8: Placement): Array2d[Placement] =
    new Array2d[Placement](Array(s0, s1, s2, s3, s4, s5, s6, s7, s8), 3, 3)
}
