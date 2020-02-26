package environment

import scalafx.scene.canvas.GraphicsContext

trait Environment {
  val pegsLeft: Int
  val board: Board
  val reward: Double
  val possibleActions: List[Action]
  def isDone: Boolean = possibleActions.isEmpty
  def render(gc: GraphicsContext): Unit = {
    board.render(gc)
  }
  def step(action: Action): Environment
  def toggleCell(x: Int, y: Int): Environment
}
