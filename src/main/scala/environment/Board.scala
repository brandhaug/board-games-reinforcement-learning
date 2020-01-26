package environment

import environment.BoardType.BoardType
import scalafx.scene.canvas.GraphicsContext

trait Board {
  val grid: List[List[Cell]]
  val boardType: BoardType
  val cellWidth: Int
  val cellHeight: Int
  def render(gc: GraphicsContext): Unit
}
