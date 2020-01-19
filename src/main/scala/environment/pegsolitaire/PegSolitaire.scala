package environment.pegsolitaire

import environment.{Action, Environment, State}
import scalafx.scene.canvas.GraphicsContext

case class PegSolitaire(pegBoard: PegBoard) extends Environment {
  def state(): State = {
    ???
  }

  def step(previousState: State, action: Action): State = {
    ???
  }

  def possibleActions(): Set[Action] = {
    ???
  }

  def render(gc: GraphicsContext): Unit = {
    pegBoard.render(gc)
  }
}


