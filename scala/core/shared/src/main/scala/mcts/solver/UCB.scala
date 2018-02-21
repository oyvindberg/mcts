package mcts.solver

/**
  * Upper Confidence Bound 1 applied to trees
  */
object UCB {
  def apply[S, A](parent:          Node[S, A],
                  child:           Node[S, A],
                  isOpponentsTurn: Boolean): Double = {

    val numWins      = if (isOpponentsTurn) -child.numWins else child.numWins
    val exploitation = numWins / child.numPlays
    val exploration = Epsilon * math.sqrt(
      math.log(parent.numPlays) / child.numPlays
    )

    exploitation + exploration
  }

  private val Epsilon = math.sqrt(2)
}
