package environment

import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

abstract class Cell {
  val x: Int
  val y: Int
  val color: Color
  def render(gc: GraphicsContext, startX: Int, startY: Int, width: Int, height: Int): Unit
}
