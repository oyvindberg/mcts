package mcts.games

import mcts.{Player, _}
import mcts.games.Array2d.{Column, Row}

case class ConnectFourState(
    board:         Array2d[Placement],
    currentPlayer: Player,
    winnerOpt:     Option[(Player, Line)]
)

case class ConnectFour(
    numRows:         Int             = 6,
    numCols:         Int             = 7,
    victoryTemplate: VictoryTemplate = VictoryTemplate.FourInRow
) extends Game[ConnectFourState, Column] {

  val FirstRow: Row = 0
  val LastRow:  Row = numRows - 1

  override val startingState: ConnectFourState =
    ConnectFourState(
      board         = Array2d(numCols, numRows, Empty),
      currentPlayer = Player.One,
      winnerOpt     = None
    )

  override def currentPlayer(state: ConnectFourState): Player =
    state.currentPlayer

  /**
    * The set of possible drops is columns where the uppermost row is empty.
    */
  def availableDrops(board: Array2d[Placement]): Array[Column] =
    Cols.filter((col: Column) => board(col, FirstRow) == Empty)

  /* Cached to avoid allocations */
  val Cols: Array[Column] = 0.until(numCols).toArray

  override def nextState(drop:  Column,
                         state: ConnectFourState): ConnectFourState = {

    val row   = dropSettlesInRow(state.board, drop)
    val board = state.board.updated(drop, row, Occupied(state.currentPlayer))

    ConnectFourState(
      board         = board,
      currentPlayer = state.currentPlayer.opponent,
      winnerOpt     = WinnerCheck(victoryLines(board.index(drop, row)), board)
    )
  }

  /* For each point on the board, we precalc possible lines which can form a victory */
  val victoryLines: Array2d[Array[Line]] =
    VictoryTemplate.linesForBoard(victoryTemplate, startingState.board)

  /**
    * The row that a piece settles in when dropped
    */
  def dropSettlesInRow(board: Array2d[Placement], col: Column): Row =
    firstDroppedInColumn(board, col) match {
      case Some(row) => row - 1
      case None      => LastRow
    }

  /**
    * The top-most piece dropped in the given column, if any
    */
  def firstDroppedInColumn(board: Array2d[Placement],
                           col:   Column): Option[Row] =
    Rows.find((row: Row) => board(col, row) != Empty)

  /* Cached to avoid allocations */
  val Rows: Array[Row] = 0.until(numRows).toArray

  override def gameResult(state: ConnectFourState): GameResult[Column] =
    state.winnerOpt match {
      case Some((winner, _)) => Winner(winner)
      case None =>
        availableDrops(state.board) match {
          case drops if drops.isEmpty => Draw
          case drops                  => Ongoing(drops)
        }
    }
}
