package mcts

/**
  * A game specification is characterized by two types.
  *
  * @tparam S Current state of game
  * @tparam A The actions a player can perform
  */
trait Game[S, A] {
  def startingState: S

  def currentPlayer(state: S): Player

  def nextState(action: A, state: S): S

  def gameResult(state: S): GameResult[A]

  def payout(gameEnded: GameEnded, player: Player): Double =
    gameEnded match {
      case Winner(winner) => if (winner == player) 1.0 else 0
      case Draw           => 0.5
    }
}

/**
  * A game result will always be in one of these three states
  */
sealed trait GameResult[+A]

case class Ongoing[A](actions: Array[A]) extends GameResult[A]

sealed trait GameEnded extends GameResult[Nothing]

case class Winner(winner: Player) extends GameEnded

case object Draw extends GameEnded
