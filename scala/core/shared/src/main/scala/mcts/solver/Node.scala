package mcts.solver

case class Node[S, A](state:      S,
                      numWins:    Double,
                      numPlays:   Int,
                      children:   Map[A, Node[S, A]],
                      lastPayout: Double) {

  def backPropagating(action: A, newChild: Node[S, A]): Node[S, A] =
    copy(
      children   = children.updated(action, newChild),
      numWins    = numWins + newChild.lastPayout,
      numPlays   = numPlays + 1,
      lastPayout = newChild.lastPayout
    )

  def backPropagatingPayout(payout: Double): Node[S, A] =
    copy(
      numWins    = numWins + payout,
      numPlays   = numPlays + 1,
      lastPayout = payout
    )

  def bestAction: (A, Node[S, A]) =
    children.maxBy {
      case (_, childNode) => childNode.numWins / childNode.numPlays
    }
}

object Node {
  def fromState[S, A](state: S): Node[S, A] = Node(state, 0, 0, Map.empty, 0)

  def fromPayout[S, A](state: S, payout: Double): Node[S, A] =
    Node(
      state      = state,
      numWins    = payout,
      numPlays   = 1,
      children   = Map.empty,
      lastPayout = payout
    )

  /**
    * Observing that an immutable tree of `Node`s can be combined,
    *  enables us to explore the tree in parallel.
    */
  def combine[S, A](x: Node[S, A], y: Node[S, A]): Node[S, A] =
    Node(
      state      = x.state,
      numWins    = x.numWins + y.numWins,
      numPlays   = x.numPlays + y.numPlays,
      children   = combineChildren(x.children, y.children),
      lastPayout = x.lastPayout + y.lastPayout
    )

  def combineChildren[S, A](xs: Map[A, Node[S, A]],
                            ys: Map[A, Node[S, A]]): Map[A, Node[S, A]] = {
    val ret = Map.newBuilder[A, Node[S, A]]
    ret ++= xs

    ys.foreach {
      case (action, node) =>
        val combinedNode: Node[S, A] =
          if (xs.contains(action)) combine(xs(action), node) else node

        ret += ((action, combinedNode))
    }
    ret.result()
  }
}
