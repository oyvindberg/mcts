package javabin.mcts

import javabin.Board
import java.util.Random

class Node(var board: Board, val parent: Node? = null, val children: MutableList<Node> = mutableListOf(), var visitCount: Int = 0, var winScore: Double = 0.0) {
    fun randomChildNode(random: Random): Node {
        val noOfPossibleMoves = this.children.size
        val randomPosition = random.nextInt(noOfPossibleMoves)
        return this.children[randomPosition]
    }

    val childWithMaxScore: Node
        get() = this.children.maxBy { it.visitCount }!!

    fun copy(): Node {
        val children = this.children.mapTo(mutableListOf()) { it.copy() }
        return Node(this.board, this.parent, children)
    }

    internal fun incrementVisit() {
        this.visitCount++
    }

    internal fun addScore(score: Double) {
        if (this.winScore != NO_WIN_SCORE)
            this.winScore += score
    }

    companion object {
        const val NO_WIN_SCORE = Integer.MIN_VALUE.toDouble()
    }

}
