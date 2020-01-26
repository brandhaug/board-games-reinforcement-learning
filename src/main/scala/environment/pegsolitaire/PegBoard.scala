package environment.pegsolitaire

import PegBoardType.PegBoardType
import main.Canvas
import scalafx.scene.canvas.GraphicsContext

case class PegBoard(grid: List[List[PegCell]], boardType: PegBoardType) {
  val cellWidth: Int = Canvas.width / grid.head.length
  val cellHeight: Int = Canvas.height / grid.length

  def render(gc: GraphicsContext): Unit = {
    for {
      (line, yIndex) <- grid.zipWithIndex
      (cell, xIndex) <- line.zipWithIndex
      _ = cell.render(
        gc,
        cellWidth * xIndex,
        cellHeight * yIndex,
        cellWidth,
        cellHeight
      )
    } yield { () }
  }
}

