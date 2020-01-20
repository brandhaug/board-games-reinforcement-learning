package environment

import scalafx.scene.canvas.GraphicsContext

abstract class Environment {
  val reward: Int
  val possibleActions: List[Action]
  def step(action: Action): Environment
  def render(gc: GraphicsContext): Unit
}
