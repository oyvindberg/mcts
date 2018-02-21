package mcts

import fansi.{Attr, Color, Str}
import mcts.Player.{One, Two}

sealed trait Player {
  def opponent: Player = this match {
    case One => Two
    case Two => One
  }
}

object Player {
  case object One extends Player
  case object Two extends Player

  def color(player: Player): Attr =
    player match {
      case One => Color.Red
      case Two => Color.Blue
    }

  def render(player: Player): Str =
    color(player)(if (player == One) "◍" else "❌")
}
