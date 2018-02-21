package mcts

import mcts.cli.{Runner, SolverInterface}
import mcts.games.{ConnectFour, TicTacToe, VictoryTemplate}

import scala.concurrent.duration._
import scala.io.StdIn

object Main {
  def pause(): Unit =
    StdIn.readLine("[Enter]")

  val TicTacToe4 = TicTacToe(
    numCols = 4,
    numRows = 5,
    VictoryTemplate.FourInRow
  )

  def main(args: Array[String]): Unit = {
    SolverInterface.step(TicTacToe())(Runner(100.millis))
    pause()

    SolverInterface.step(TicTacToe4)(Runner(100.millis).parallel())
    pause()

    SolverInterface.step(ConnectFour())(Runner(1000.millis).parallel(4))
    pause()

  }
}
