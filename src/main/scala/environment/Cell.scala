package environment

import environment.BoardType.BoardType
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

trait Cell {
  val x: Int
  val y: Int
  val color: Color
  val strokeColor: Color
  val boardType: BoardType
  def render(gc: GraphicsContext, startX: Int, startY: Int, width: Int, height: Int): Unit
}
