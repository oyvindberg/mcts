package mcts.solver

import java.util.concurrent.ThreadLocalRandom

import mcts.{Game, GameEnded, Ongoing, Player}

case class MonteCarlo[S, A](game: Game[S, A]) {

  /* one iteration of the simulation. */
  def select(currentNode: Node[S, A], player: Player): Node[S, A] =
    game.gameResult(currentNode.state) match {
      case gameEnded: GameEnded =>
        currentNode.backPropagatingPayout(game.payout(gameEnded, player))

      case Ongoing(actions) =>
        /* Look for an unexpanded action to start simulation from. */
        val unexpandedActionOpt: Option[A] =
          actions.find(a => !currentNode.children.contains(a))

        unexpandedActionOpt match {
          /* Perform expansion by applying the action and then simulate randomly until game ends */
          case Some(actionToExpand) =>
            val expandedNode: Node[S, A] =
              expandAction(currentNode, player, actionToExpand)

            currentNode.backPropagating(actionToExpand, expandedNode)

          /* Recursively call select on a child of this tree chosen with UCB. */
          case None =>
            val isOpponentsTurn: Boolean =
              game.currentPlayer(currentNode.state) != player

            val (action, actionNode: Node[S, A]) =
              currentNode.children.maxBy {
                case (_, childNode) =>
                  UCB(currentNode, childNode, isOpponentsTurn)
              }

            val exploredNode: Node[S, A] = select(actionNode, player)

            currentNode.backPropagating(action, exploredNode)
        }
    }

  private def expandAction(currentNode:    Node[S, A],
                           player:         Player,
                           actionToExpand: A): Node[S, A] = {

    val nextState: S         = game.nextState(actionToExpand, currentNode.state)
    val gameEnded: GameEnded = simulateRandomlyUntilGameEnds(nextState)
    val payout:    Double    = game.payout(gameEnded, player)
    Node.fromPayout(nextState, payout)
  }

  private def simulateRandomlyUntilGameEnds(state: S): GameEnded =
    game.gameResult(state) match {
      case gameEnded: GameEnded => gameEnded

      case Ongoing(actions) =>
        val randomAction = actions(
          ThreadLocalRandom.current().nextInt(actions.length)
        )
        val stateAfterAction = game.nextState(randomAction, state)

        simulateRandomlyUntilGameEnds(stateAfterAction)
    }
}
