package mcts.games

import mcts.games.Array2d.{Column, Index, Row}

/* A point relative to point */
case class RelPoint(x: Column, y: Row)

/* A line relative to a point */
case class RelLine(relPoints: RelPoint*)

case class Line(indices: Array[Index])
