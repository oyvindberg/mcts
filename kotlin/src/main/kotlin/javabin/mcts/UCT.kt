package javabin.mcts

import kotlin.math.sqrt

object UCT {
    private val EPSILON: Double = sqrt(2.0)
    private val max = Integer.MAX_VALUE.toDouble()

    fun uctValue(totalVisit: Int, nodeWinScore: Double, nodeVisit: Int): Double {
        return if (nodeVisit == 0) max else {
            nodeWinScore / nodeVisit.toDouble() + EPSILON *
                    sqrt(log(totalVisit.toDouble()) / nodeVisit.toDouble())
        }
    }

    private fun log(value: Double): Double = kotlin.math.log(value, kotlin.math.E)

    internal fun findBestNodeWithUCT(node: Node): Node {
        val parentVisit = node.visitCount
        return node.children.maxBy { uctValue(parentVisit, it.winScore, it.visitCount) }!!
    }
}
