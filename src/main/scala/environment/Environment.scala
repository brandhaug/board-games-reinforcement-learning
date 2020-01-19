package environment

import scalafx.scene.canvas.GraphicsContext

abstract class Environment {
  def state(): State
  def step(previousState: State, action: Action): State
  def possibleActions(): Set[Action]
  def render(gc: GraphicsContext): Unit
}
