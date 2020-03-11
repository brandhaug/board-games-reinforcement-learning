package environment

import environment.BoardType.BoardType
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Polygon, StrokeType}
import utils.GraphicsUtils

trait Cell {
  val xIndex: Int
  val yIndex: Int
  val color: Color
  val strokeColor: Color
  val boardType: BoardType
  val cellType: Int
  val isEmpty: Boolean
  val isNone: Boolean
  val isNonEmpty: Boolean
  def render(gc: GraphicsContext, startX: Double, startY: Double, width: Double, height: Double): Unit = {
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
      case BoardType.Hex =>
        GraphicsUtils.fillHexagon(gc, xIndex, yIndex, width, height)
      case _ => throw new Exception("Unknown BoardType")
    }
  }
}
