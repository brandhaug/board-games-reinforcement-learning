package environment

import java.util

import environment.BoardType.BoardType
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

trait Cell {
  val xIndex: Int
  val yIndex: Int
  val color: Color
  val strokeColor: Color
  val boardType: BoardType
  val cellValue: Int
  def render(gc: GraphicsContext, startX: Int, startY: Int, width: Int, height: Int): Unit = {
    gc.setFill(color)
    gc.setStroke(strokeColor)

    boardType match {
      case BoardType.Square =>
        gc.fillRect(startX, startY, width, height)
        gc.strokeRect(startX, startY, width, height)
      case BoardType.Triangular =>
        gc.fillOval(startX, startY, width - 5, height)
      case BoardType.Diamond =>
        gc.fillOval(startX, startY, width - 5, height)
      case _ => throw new Exception("Unknown BoardType")
    }
  }
}
