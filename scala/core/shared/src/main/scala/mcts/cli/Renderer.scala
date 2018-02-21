package mcts.cli

import fansi.Str
import mcts.Player
import mcts.games.Array2d.{Column, Row}
import mcts.games._

/**
  * This is the typeclass pattern.
  */
trait Renderer[T] {
  def render(t: T): Str
}

object Renderer {
  def apply[T: Renderer](t: T): Str = implicitly[Renderer[T]].render(t)

  def renderSpace(
      board:       Array2d[Placement],
      col:         Column,
      row:         Row,
      victoryLine: Option[(Player, Line)]
  ): Str =
    board(col, row) match {
      case Occupied(player) =>
        victoryLine match {
          case Some((_, vl)) if vl.indices.contains(board.index(col, row)) =>
            Player.color(player)("*")
          case _ => Player.render(player)
        }
      case Empty => "◌"
    }

  implicit val RenderConnectFour: Renderer[ConnectFourState] =
    (state: ConnectFourState) => {
      val Cols   = 0.until(state.board.numCols)
      val h1     = Cols.map(_ => "═").mkString("╔═", "═╤═", "═╗\n")
      val h2     = Cols.map(identity).mkString("║ ", " │ ", " ║\n")
      val h3     = Cols.map(_ => "═").mkString("╠═", "═╪═", "═╣\n")
      val footer = Cols.map(_ => "═").mkString("╚═", "═╧═", "═╝\n")

      val table = 0
        .until(state.board.numRows)
        .flatMap(
          row =>
            Cols
              .map(col => renderSpace(state.board, col, row, state.winnerOpt))
              .mkString("│ ", " │ ", " │\n")
        )

      h1 ++ h2 ++ h3 ++ table ++ footer
    }

  implicit val RenderTicTacToe: Renderer[TicTacToeState] =
    (state: TicTacToeState) => {
      val Cols   = 0.until(state.board.numCols)
      val h1     = Cols.map(_ => "═══").mkString("╔", "╤", "╗\n")
      val footer = Cols.map(_ => "═══").mkString("╚", "╧", "╝\n")
      val table = 0
        .until(state.board.numRows)
        .flatMap(
          row =>
            Cols
              .map(col => renderSpace(state.board, col, row, state.winnerOpt))
              .mkString("│ ", " │ ", " │\n")
        )

      h1 ++ table ++ footer
    }
}
