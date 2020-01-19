package pegsolitaire

import main.Canvas
import pegsolitaire.BoardType.BoardType
import scalafx.scene.canvas.GraphicsContext

case class Board(grid: List[List[Cell]], boardType: BoardType) {
  val cellWidth: Int = Canvas.width / grid.head.length
  val cellHeight: Int = Canvas.height / grid.length

  def render(gc: GraphicsContext): Unit = {
    for {
      (line, yIndex) <- grid.zipWithIndex
      (cell, xIndex) <- line.zipWithIndex
      _ = cell.render(gc, cellWidth * xIndex, cellHeight * yIndex, cellWidth, cellHeight)
    } yield {
      ()
    }
  }
}