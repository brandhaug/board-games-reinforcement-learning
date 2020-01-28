package environment.pegsolitaire

import environment.BoardType.BoardType
import environment.{BoardType, Cell}
import environment.pegsolitaire.PegCellType.PegCellType
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

case class PegCell(xIndex: Int, yIndex: Int, cellType: PegCellType, boardType: BoardType) extends Cell {
  val isEmpty: Boolean = cellType == PegCellType.Empty
  val isNone: Boolean = cellType == PegCellType.None
  val isPeg: Boolean = cellType == PegCellType.Peg

  val color: Color = {
    cellType match {
      case PegCellType.Peg   => Color.Red
      case PegCellType.Empty => Color.White
      case PegCellType.None  => Color.Transparent
    }
  }

  val strokeColor: Color = {
    cellType match {
      case PegCellType.Peg   => Color.Black
      case PegCellType.Empty => Color.Black
      case PegCellType.None  => Color.Transparent
    }
  }

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
