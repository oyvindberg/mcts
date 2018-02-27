package javabin.mcts

import javabin.Board
import javabin.Player
import javabin.Position
import javabin.Status
import java.time.Duration
import java.util.Random

object MonteCarloTreeSearch {
    private val WIN_SCORE = 10.0

    fun findNextMove(board: Board, player: Player, random: Random, duration: Duration): Board {
        val start = System.currentTimeMillis()
        val end = start + duration.toMillis()

        val opponent = player.opponent
        val rootNode = Node(board.withPlayer(opponent))

        while (System.currentTimeMillis() < end) {
            // Phase 1 - Selection
            val promisingNode = selectPromisingNode(rootNode)
            // Phase 2 - Expansion
            val status = promisingNode.board.checkStatus()
            if (status is Status.InProgress)
                expandNode(promisingNode, status.positions)

            // Phase 3 - Simulation
            val nodeToExplore = if (promisingNode.children.isNotEmpty()) {
                promisingNode.randomChildNode(random)
            } else promisingNode

            val playoutResult = simulateRandomPlayout(nodeToExplore, opponent, random)
            // Phase 4 - Update
            backPropogation(nodeToExplore, playoutResult)
        }

        val winnerNode = rootNode.childWithMaxScore
        return winnerNode.board
    }

    private fun selectPromisingNode(rootNode: Node): Node {
        var node = rootNode
        while (node.children.isNotEmpty()) {
            node = UCT.findBestNodeWithUCT(node)
        }
        return node
    }

    private fun expandNode(node: Node, positions: List<Position>) {
        val possibleStates = node.board.allPossibleMoves(node.board.currentPlayer.opponent, positions)
        node.children.addAll(possibleStates.map { state -> Node(state, node) })
    }

    private fun backPropogation(nodeToExplore: Node, status: Status) {
        var tempNode: Node? = nodeToExplore
        while (tempNode != null) {
            tempNode.incrementVisit()
            if (status is Status.Win && tempNode.board.currentPlayer == status.player)
                tempNode.addScore(WIN_SCORE)
            tempNode = tempNode.parent
        }
    }

    private fun simulateRandomPlayout(node: Node, opponent: Player, random: Random): Status {
        val tempNode = node.copy()
        //val tempState = tempNode.state
        var boardStatus = tempNode.board.checkStatus()

        if (boardStatus == Status.Win(opponent)) {
            tempNode.parent?.winScore = Node.NO_WIN_SCORE
            return boardStatus
        }
        while (boardStatus is Status.InProgress) {
            boardStatus = randomPlay(boardStatus.positions, random, tempNode)
        }

        return boardStatus
    }

    private fun randomPlay(positions: List<Position>, random: Random, tempNode: Node): Status {
        val randomPosition = random.nextInt(positions.size)
        tempNode.board = tempNode.board.withMove(tempNode.board.currentPlayer.opponent, positions[randomPosition])
        return tempNode.board.checkStatus()
    }
}
