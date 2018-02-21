// scalafmt: { maxColumn = 110 }
package mcts.cli

import java.lang.System.currentTimeMillis

import mcts.solver.Node

import scala.concurrent.duration._

/**
  * Decide *how* to run the simulation - sequentially or in parallel, and after how long to stop it
  */
trait Runner { outer =>

  def apply[S, A](initial: Node[S, A])(iter: Node[S, A] => Node[S, A]): Node[S, A]

  /**
    * By providing a function to combine two values, we can perform the work in parallel. JVM only
    */
  final def parallel(parallelism: Int = math.max(Runtime.getRuntime.availableProcessors - 1, 1)): Runner =
    new Runner {
      override def apply[S, A](initial: Node[S, A])(iter: Node[S, A] => Node[S, A]): Node[S, A] =
        (0 until parallelism).par
          .map(_ => outer(initial)(iter))
          .seq
          .reduce(Node.combine[S, A])
    }
}

object Runner {
  def apply(maxTime: Duration = 1000.millis, maxIterations: Long = 100000): Runner =
    new Runner {
      def apply[S, A](n: Node[S, A])(iter: Node[S, A] => Node[S, A]): Node[S, A] = {
        val stopTime = currentTimeMillis + maxTime.toMillis
        var iterations = 0L
        var ret: Node[S, A] = n

        while (iterations < maxIterations && currentTimeMillis < stopTime) {
          ret = iter(ret)
          iterations += 1
        }
        ret
      }
    }
}
