package mcts.games

import mcts.Player

sealed trait Placement
case object Empty extends Placement
case class Occupied(player: Player) extends Placement
