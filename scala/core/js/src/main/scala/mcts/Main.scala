package mcts

import mcts.cli.{Runner, SolverInterface}
import mcts.games.ConnectFour
import org.scalajs.dom

import scala.concurrent.duration._
import scala.scalajs.js

object Main {
  /* If we run in a browser show debugger, else run ConnectFour */
  def main(args: Array[String]): Unit =
    if (!js.isUndefined(dom.document)) {
      Debugger.start()
    } else {
      SolverInterface.step(ConnectFour())(Runner(1000.millis))
    }
}
