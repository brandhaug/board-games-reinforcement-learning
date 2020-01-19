package pegsolitaire

import reinforcementlearning.State
import scalafx.scene.canvas.GraphicsContext

object PegSolitaire {
  def state(board: Board): State = {
    ???
  }

  def step(previousState: State, action: Int): State = {
    ???
  }

  def possibleActions(board: Board): Unit = {
    ???
  }

  def render(gc: GraphicsContext, state: State): Unit = {
    ???
  }
}
