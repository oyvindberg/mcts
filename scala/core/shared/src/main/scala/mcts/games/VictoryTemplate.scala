package mcts.games

import mcts.games.Array2d.{Column, Index, Row}

case class VictoryTemplate(relLines: RelLine*)

object VictoryTemplate {

  // format: off

  val TwoInRow: VictoryTemplate =
    VictoryTemplate(
      /* horizontal */
      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  1, y =  0)),
      RelLine(RelPoint(x = -1, y =  0), RelPoint(x =  0, y =  0)),

      /* vertical */
      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  0, y =  1)),
      RelLine(RelPoint(x =  0, y = -1), RelPoint(x =  0, y =  0)),

      /* diagonal */
      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  1, y =  1)),
      RelLine(RelPoint(x = -1, y = -1), RelPoint(x =  0, y =  0)),

      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  1, y = -1)),
      RelLine(RelPoint(x = -1, y =  1), RelPoint(x =  0, y =  0)),
    )

  val ThreeInRow: VictoryTemplate =
    VictoryTemplate(
      /* horizontal */
      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  1, y =  0), RelPoint(x =  2, y =  0)),
      RelLine(RelPoint(x = -1, y =  0), RelPoint(x =  0, y =  0), RelPoint(x =  1, y =  0)),
      RelLine(RelPoint(x = -2, y =  0), RelPoint(x = -1, y =  0), RelPoint(x =  0, y =  0)),

      /* vertical */
      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  0, y =  1), RelPoint(x =  0, y =  2)),
      RelLine(RelPoint(x =  0, y = -1), RelPoint(x =  0, y =  0), RelPoint(x =  0, y =  1)),
      RelLine(RelPoint(x =  0, y = -2), RelPoint(x =  0, y = -1), RelPoint(x =  0, y =  0)),

      /* diagonal */
      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  1, y =  1), RelPoint(x =  2, y =  2)),
      RelLine(RelPoint(x = -1, y = -1), RelPoint(x =  0, y =  0), RelPoint(x =  1, y =  1)),
      RelLine(RelPoint(x = -2, y = -2), RelPoint(x = -1, y = -1), RelPoint(x =  0, y =  0)),

      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  1, y = -1), RelPoint(x =  2, y = -2)),
      RelLine(RelPoint(x = -1, y =  1), RelPoint(x =  0, y =  0), RelPoint(x =  1, y = -1)),
      RelLine(RelPoint(x = -2, y =  2), RelPoint(x = -1, y =  1), RelPoint(x =  0, y =  0))
    )

  val FourInRow: VictoryTemplate =
    VictoryTemplate(
      /* horizontal */
      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  1, y =  0), RelPoint(x =  2, y =  0), RelPoint(x = 3, y = 0)),
      RelLine(RelPoint(x = -1, y =  0), RelPoint(x =  0, y =  0), RelPoint(x =  1, y =  0), RelPoint(x = 2, y = 0)),
      RelLine(RelPoint(x = -2, y =  0), RelPoint(x = -1, y =  0), RelPoint(x =  0, y =  0), RelPoint(x = 1, y = 0)),
      RelLine(RelPoint(x = -3, y =  0), RelPoint(x = -2, y =  0), RelPoint(x = -1, y =  0), RelPoint(x = 0, y = 0)),

      /* vertical */
      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  0, y =  1), RelPoint(x =  0, y =  2), RelPoint(x = 0, y = 3)),
      RelLine(RelPoint(x =  0, y = -1), RelPoint(x =  0, y =  0), RelPoint(x =  0, y =  1), RelPoint(x = 0, y = 2)),
      RelLine(RelPoint(x =  0, y = -2), RelPoint(x =  0, y = -1), RelPoint(x =  0, y =  0), RelPoint(x = 0, y = 1)),
      RelLine(RelPoint(x =  0, y = -3), RelPoint(x =  0, y = -2), RelPoint(x =  0, y = -1), RelPoint(x = 0, y = 0)),

      /* diagonal */
      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  1, y =  1), RelPoint(x =  2, y =  2), RelPoint(x = 3, y = 3)),
      RelLine(RelPoint(x = -1, y = -1), RelPoint(x =  0, y =  0), RelPoint(x =  1, y =  1), RelPoint(x = 2, y = 2)),
      RelLine(RelPoint(x = -2, y = -2), RelPoint(x = -1, y = -1), RelPoint(x =  0, y =  0), RelPoint(x = 1, y = 1)),
      RelLine(RelPoint(x = -3, y = -3), RelPoint(x = -2, y = -2), RelPoint(x = -1, y = -1), RelPoint(x = 0, y = 0)),

      RelLine(RelPoint(x =  0, y =  0), RelPoint(x =  1, y = -1), RelPoint(x =  2, y = -2), RelPoint(x = 3, y = -3)),
      RelLine(RelPoint(x = -1, y =  1), RelPoint(x =  0, y =  0), RelPoint(x =  1, y = -1), RelPoint(x = 2, y = -2)),
      RelLine(RelPoint(x = -2, y =  2), RelPoint(x = -1, y =  1), RelPoint(x =  0, y =  0), RelPoint(x = 1, y = -1)),
      RelLine(RelPoint(x = -3, y =  3), RelPoint(x = -2, y =  2), RelPoint(x = -1, y =  1), RelPoint(x = 0, y =  0))
    )
  // format: on

  def linesForBoard[T](template: VictoryTemplate,
                       board:    Array2d[T]): Array2d[Array[Line]] =
    board.map(
      (col, row, _) =>
        template.relLines.toArray
          .flatMap(relLine => validLine(relLine, col, row, board))
    )

  /**
    * A line for (col, row) if it is completely within bounds of `board`
    */
  private def validLine[T](relLine:     RelLine,
                           absoluteCol: Column,
                           absoluteRow: Row,
                           dimensions:  Array2d[T]): Option[Line] = {

    val points         = Array.ofDim[Index](relLine.relPoints.length)
    var idx            = 0
    var isWithinBounds = true

    while (idx < relLine.relPoints.length && isWithinBounds) {
      val relPoint: RelPoint = relLine.relPoints(idx)
      val col:      Column   = absoluteCol + relPoint.x
      val row:      Row      = absoluteRow + relPoint.y

      if (col < dimensions.numCols && col >= 0 && row < dimensions.numRows && row >= 0)
        points(idx) = dimensions.index(col, row)
      else
        isWithinBounds = false

      idx += 1
    }

    if (isWithinBounds) Some(Line(points)) else None
  }
}
