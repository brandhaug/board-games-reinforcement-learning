package environment.pegsolitaire

import environment.pegsolitaire.enums.PegBoardType.PegBoardType
import environment.pegsolitaire.enums.PegCellType
import environment.pegsolitaire.enums.PegCellType.PegCellType
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color

case class PegCell(cellType: PegCellType, boardType: PegBoardType) {
  val color: Color = {
    cellType match {
      case PegCellType.Peg   => Color.Red
      case PegCellType.Empty => Color.White
      case PegCellType.None  => Color.Black
    }
  }

  val neighbors: List[PegCell] = {
    List.empty
  }

  def render(gc: GraphicsContext,
             startX: Int,
             startY: Int,
             width: Int,
             height: Int): Unit = {
    gc.setFill(color)
    gc.setStroke(Color.Black)
    gc.fillRect(startX, startY, width, height)
    gc.strokeRect(startX, startY, width, height)

  }
}

