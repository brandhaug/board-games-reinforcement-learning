package environment.pegsolitaire

import environment.BoardType.BoardType
import environment.Cell
import environment.pegsolitaire.PegCellType.PegCellType
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

case class PegCell(x: Int, y: Int, cellType: PegCellType, boardType: BoardType) extends Cell {
  val isEmpty: Boolean = cellType == PegCellType.Empty
  val isNone: Boolean = cellType == PegCellType.None
  val isPeg: Boolean = cellType == PegCellType.Peg

  val color: Color = {
    cellType match {
      case PegCellType.Peg   => Color.Red
      case PegCellType.Empty => Color.White
      case PegCellType.None  => Color.Black
    }
  }

  def render(gc: GraphicsContext, startX: Int, startY: Int, width: Int, height: Int): Unit = {
    gc.setFill(color)
    gc.setStroke(Color.Black)
    gc.fillRect(startX, startY, width, height)
    gc.strokeRect(startX, startY, width, height)
  }
}
