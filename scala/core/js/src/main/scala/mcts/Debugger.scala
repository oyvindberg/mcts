package mcts

import mcts.cli.{Renderer, Runner, SolverInterface}
import mcts.games.Array2d.Index
import mcts.games.{TicTacToe, TicTacToeState, VictoryTemplate}
import mcts.solver.{Node, UCB}
import org.scalajs.dom
import org.scalajs.dom.ext.KeyCode
import org.scalajs.dom.html.{Div, Paragraph}
import org.scalajs.dom.raw.KeyboardEvent
import scalatags.JsDom
import scalatags.JsDom.all._

import scala.collection.mutable
import scala.concurrent.duration._
import scala.math.Ordering

/**
  * This was written for the algorithm intro
  */
object Debugger {
  val SimpleTicTacToe = TicTacToe(3, 1, VictoryTemplate.TwoInRow)

  val color = Seq(
    "#DECC8C",
    "#DE986D",
    "#AB6890",
    "#68AB9F",
    "#DEC371",
    "red",
    "orange",
    "pink",
    "white",
    "blue",
  )

  def start(): Unit = {
    /* Capture nodes at every step of the simulation so we can render them. */
    object CapturingRunner extends Runner {
      type N = Node[TicTacToeState, Index]
      val base = Runner(1000.millis, 10000)

      val nodeHistory = mutable.ArrayBuffer.empty[N]

      override def apply[S, A](
          initial: Node[S, A]
      )(iter:      Node[S, A] => Node[S, A]): Node[S, A] = {
        // yeah, this *is* a hack.
        nodeHistory += initial.asInstanceOf[N]
        base(initial) { node =>
          val ret = iter(node)
          nodeHistory += ret.asInstanceOf[N]
          ret
        }
      }
    }

    SolverInterface.step(SimpleTicTacToe)(CapturingRunner)

    showHistory(CapturingRunner.nodeHistory)
  }

  def showHistory(nodeHistory: Seq[Node[TicTacToeState, Index]]): Unit = {
    val myDiv: Div =
      div(
        textAlign      := "center",
        display        := "flex",
        justifyContent := "center"
      ).render

    dom.document.body.outerHTML = ""
    dom.document.body.appendChild(myDiv)

    var idx = 0

    dom.document.onkeydown = (e: KeyboardEvent) => {
      e.keyCode match {
        case KeyCode.Left =>
          idx = math.max(0, idx - diff(e))
        case KeyCode.Right =>
          idx = math.min(nodeHistory.length - 1, idx + diff(e))
        case _ => e.stopPropagation()
      }
      render()
    }

    def render(): Unit = {
      myDiv.innerHTML = ""
      myDiv.appendChild(
        renderNode(None, None, 0, nodeHistory(idx)).render
      )
    }

    render()
  }

  def diff(e: KeyboardEvent): Int =
    (e.getModifierState("Shift"), e.getModifierState("Control")) match {
      case (true, true)   => 1000
      case (false, true)  => 100
      case (true, false)  => 10
      case (false, false) => 1
    }

  def renderDecimal(d: Double): String =
    f"$d%.2f"

  def row(title: String, value: JsDom.Frag): JsDom.TypedTag[Paragraph] =
    p(title, ": ", b(fontWeight := "900", value))

  def renderNode[S: Renderer, A: Ordering](
      action: Option[A],
      parent: Option[Node[S, A]],
      depth:  Int,
      n:      Node[S, A]
  ): JsDom.TypedTag[Div] =
    div(
      margin          := "0.5rem",
      padding         := "1rem",
      backgroundColor := color(depth),
      fontWeight      := "800",
      cls             := "card",
      action.map(a => h2(s"Action $a")),
      div(
        pre(code(Renderer(n.state).plainText))
      ),
      div(
        row("played", n.numPlays),
        row("wins", n.numWins),
        row("ratio",
            renderDecimal(if (n.numPlays > 0) n.numWins / n.numPlays else 0)),
        row("children", n.children.size),
        parent.map(
          parent => row("UCB", renderDecimal(UCB(parent, n, depth % 2 == 0)))
        ),
      ),
      div(
        padding       := "0.5rem",
        flexDirection := "row",
        flexWrap      := "wrap",
        display       := "flex",
        if (depth < 4)
        n.children.toList.sortBy(_._1) map {
          case (childAction, child) =>
            div(
              display := "flex",
              renderNode(Option(childAction), Option(n), depth + 1, child)
            )
        } else div()
      )
    )
}
