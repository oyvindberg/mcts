// format: off
package mcts.advanced

import cats.data.Ior
import cats.data.Ior.{both, left, right}
import fansi.Str
import mcts.{Player, _}
import mcts.cli.Renderer

/**
  * Enables playing two games at the same time.
  *
  * It's ok that it looks scary - it's very abstract.
  *
  * The point is to demonstrate that composition is very powerful,
  *  and you get it almost for free once you have embraced
  *  this level of abstraction
  */
object Simultaneously {

  def simultaneously[S1, S2, A1, A2](g1: Game[S1, A1], g2: Game[S2, A2]): Game[(S1, S2), Ior[A1, A2]] =
    new Game[(S1, S2), Ior[A1, A2]] {
      override val startingState: (S1, S2) =
        (g1.startingState, g2.startingState)

      override def currentPlayer(state: (S1, S2)): Player =
        // strong indication that `Game` shouldn't own current player
        g1.currentPlayer(state._1)

      override def nextState(action: Ior[A1, A2], state: (S1, S2)): (S1, S2) =
        (action, state) match {
          case (Ior.Left(a1)    , (sl, sr)) => (g1.nextState(a1, sl), sr)
          case (Ior.Right(a2)   , (sl, sr)) => (sl,                   g2.nextState(a2, sr))
          case (Ior.Both(a1, ar), (sl, sr)) => (g1.nextState(a1, sl), g2.nextState(ar, sr))
        }

      override def gameResult(state: (S1, S2)): GameResult[Ior[A1, A2]] =
        (g1.gameResult(state._1), g2.gameResult(state._2)) match {
          case (Ongoing(a1s)    , Ongoing(a2s))     => Ongoing(a1s.flatMap(a1 => a2s.map(a2 => both(a1, a2))))
          case (Ongoing(a1s)    , Draw | Winner(_)) => Ongoing(a1s.map(left))
          case (Draw | Winner(_), Ongoing(a2s))     => Ongoing(a2s.map(right))
          case (Draw            , Draw)             => Draw
          case (Winner(p)       , Draw)             => Winner(p)
          case (Draw            , Winner(p))        => Winner(p)
          case (Winner(p1)      , Winner(p2))       => if (p1 == p2) Winner(p1) else Draw
        }

      override def payout(gameEnded: GameEnded, player: Player): Double =
        (g1.payout(gameEnded, player) + g2.payout(gameEnded, player)) / 2
    }

  implicit def RenderSimultaneously[S1: Renderer, S2: Renderer]: Renderer[(S1, S2)] = {
    case (stateLeft, stateRight) =>
      val linesLeft:  List[Str] = lines(Renderer(stateLeft))
      val linesRight: List[Str] = lines(Renderer(stateRight))
      val height:     Int       = linesLeft.length.max(linesRight.length)
      val widthLeft:  Int       = linesLeft.map(_.length).max
      val widthRight: Int       = linesRight.map(_.length).max

      def pad(lines: List[Str], height: Int, width: Int): List[Str] =
        lines
          .map(line => line ++ (" " * (width - line.length)))
          .padTo(height, " " * width: Str)

      pad(linesLeft, height, widthLeft)
        .zip(Array.fill(height - 1)(" | "))
        .zip(pad(linesRight, height, widthRight))
        .map { case ((x1, x2), x3) => x1 ++ x2 ++ x3 ++ "\n" }
        .reduce(_ ++ _)
    }

  private def lines(str: Str): List[Str] = {
    def go(str: Str, ret: List[Str]): List[Str] = {
      var idx = 0
      while (idx < str.length) {
        if (str.getChar(idx) == '\n') {
          return go(str.substring(idx + 1), str.substring(0, idx) :: ret)
        }
        idx += 1
      }
      str :: ret
    }

    go(str, Nil).reverse
  }
}
