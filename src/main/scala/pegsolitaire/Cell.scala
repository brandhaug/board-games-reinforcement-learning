package pegsolitaire

import pegsolitaire.CellType.CellType
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

case class Cell(cellType: CellType) {
  val color: Color = {
    cellType match {
      case CellType.Peg   => Color.Red
      case CellType.Empty => Color.White
      case CellType.None  => Color.Black
    }
  }

  def neighbors(): List[Cell] = {
    ???
  }

  def render(gc: GraphicsContext, startX: Int, startY: Int, width: Int, height: Int): Unit = {
    gc.setFill(color)
    gc.setStroke(Color.Black)
    gc.setLineWidth(1)
    gc.fillRect(startX, startY, width, height)
    gc.strokeRect(startX, startY, width, height)
  }
}

