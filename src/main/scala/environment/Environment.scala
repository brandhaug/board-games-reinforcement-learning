package environment

import environment.ActionType.ActionType
import scalafx.scene.canvas.GraphicsContext

trait Environment {
  val pegsLeft: Int
  val board: Board
  val reward: Double
  val possibleActions: List[Action]
  val isDone: Boolean
  val actionTypes: Set[ActionType]
  def step(action: Action): Environment
  def render(gc: GraphicsContext): Unit
  def toggleCell(x: Int, y: Int): Environment
}
