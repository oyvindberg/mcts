package mcts.games

import mcts.Player

object WinnerCheck {

  /**
    * If any of the lines are all occupied by the same player, return the line and player
    *
    * This is a hot path, so it's somewhat optimized
    */
  def apply(lines: Array[Line],
            board: Array2d[Placement]): Option[(Player, Line)] = {

    var idx = 0
    while (idx < lines.length) {
      val wasWinner = lineIsWinner(board, lines(idx))

      if (wasWinner.isDefined) {
        return wasWinner
      }
      idx += 1
    }

    None
  }

  private def lineIsWinner(board: Array2d[Placement],
                           line:  Line): Option[(Player, Line)] =
    board(line.indices(0)) match {
      /* If the first index is occupied, check whether the rest are occupied by same player */
      case o: Occupied =>
        var idx       = 1
        var isWinning = true
        while (idx < line.indices.length && isWinning) {
          if (board(line.indices(idx)) != o) {
            isWinning = false
          }
          idx += 1
        }
        if (isWinning) Some((o.player, line))
        else None
      case _ => None
    }
}
