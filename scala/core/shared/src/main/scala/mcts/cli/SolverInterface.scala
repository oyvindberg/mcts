package mcts.cli

import fansi.{Bold, Str}
import mcts._
import mcts.solver.{MonteCarlo, Node}

object SolverInterface {

  // format: off
  def step[S: Renderer, A]
          (game:      Game[S, A])
          (runner:    Runner,
           state:     S = game.startingState,
           turn:      Int = 0,
           summaries: List[Str] = Nil): Unit = {
    // format: on

    //reset terminal
    println("\u001b[2J")
    //move cursor to start of screen
    println("\u001b[H")
    println(Renderer(state))
    summaries.take(10).foreach(println)

    game.gameResult(state) match {
      case Draw =>
        println(Bold.On("It's a Tie!"))

      case Winner(winner) =>
        println(Bold.On(s"$winner won"))

      case Ongoing(_) =>
        val player      = game.currentPlayer(state)
        val initialNode = Node.fromState[S, A](state)

        val newNode = runner(initialNode)(
          node => MonteCarlo(game).select(node, player)
        )

        val (chosenAction, chosenNode) = newNode.bestAction

        val turnSummary: Str =
          Player.color(player)(
            s"[$turn] chose $chosenAction after ${newNode.numPlays} simulations. " +
              f"${chosenNode.numWins / chosenNode.numPlays}%.2f chance of win"
          )

        step(game)(runner, chosenNode.state, turn + 1, turnSummary :: summaries)
    }
  }
}
