package environment

import environment.EnvironmentType.EnvironmentType
import scalafx.scene.canvas.GraphicsContext
import utils.ListUtils

trait Environment {
  val board: Board
  val environmentType: EnvironmentType
  val nonEmptyCells: Int  = ListUtils.sum(board.grid.map(_.count(_.isNonEmpty)))
  val emptyCells: Int  = ListUtils.sum(board.grid.map(_.count(_.isEmpty)))
  val reward: Double
  val possibleActions: List[Action]
  def isDone: Boolean = possibleActions.isEmpty
  def render(gc: GraphicsContext): Unit = {
    board.render(gc)
  }
  def step(action: Action): Environment
  def toggleCell(x: Int, y: Int): Environment
}
