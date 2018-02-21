package mcts.games

import mcts.{Player, _}
import mcts.games.Array2d.Index

case class TicTacToeState(board:         Array2d[Placement],
                          currentPlayer: Player,
                          winnerOpt:     Option[(Player, Line)])

case class TicTacToe(
    numCols:         Int             = 3,
    numRows:         Int             = 3,
    victoryTemplate: VictoryTemplate = VictoryTemplate.ThreeInRow
) extends Game[TicTacToeState, Index] {

  override val startingState: TicTacToeState =
    TicTacToeState(
      board         = Array2d(numCols, numRows, Empty),
      currentPlayer = Player.One,
      winnerOpt     = None
    )

  override def currentPlayer(s: TicTacToeState): Player =
    s.currentPlayer

  override def nextState(index: Index,
                         state: TicTacToeState): TicTacToeState = {

    val newBoard = state.board.updated(index, Occupied(state.currentPlayer))
    TicTacToeState(
      board         = newBoard,
      currentPlayer = state.currentPlayer.opponent,
      winnerOpt     = WinnerCheck(victoryLines(index), newBoard)
    )
  }

  /* For each point on the board, we precalc possible lines which can form a victory */
  val victoryLines: Array2d[Array[Line]] =
    VictoryTemplate.linesForBoard(victoryTemplate, startingState.board)

  override def gameResult(state: TicTacToeState): GameResult[Index] =
    state.winnerOpt match {
      case Some((player, _)) => Winner(player)
      case None =>
        possibleActions(state.board) match {
          case actions if actions.isEmpty => Draw
          case actions                    => Ongoing(actions)
        }
    }

  def possibleActions(board: Array2d[Placement]): Array[Index] =
    Indices.filter((idx: Index) => board(idx) == Empty)

  /* cached to avoid allocations */
  val Indices: Array[Index] = 0.until(numRows * numCols).toArray
}
