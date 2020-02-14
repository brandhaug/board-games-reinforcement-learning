package environment

import environment.BoardType.BoardType
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

trait Cell {
  val xIndex: Int
  val yIndex: Int
  val color: Color
  val strokeColor: Color
  val boardType: BoardType
  def render(gc: GraphicsContext, startX: Int, startY: Int, width: Int, height: Int): Unit
  def cellValue: Int
}
