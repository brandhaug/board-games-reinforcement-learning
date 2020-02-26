package environment

import environment.BoardType.BoardType
import applications.mcts.Window
import scalafx.scene.canvas.GraphicsContext

trait Board {
  val grid: List[List[Cell]]
  val boardType: BoardType
  val cellWidth: Int = Window.width / grid.head.length
  val cellHeight: Int = Window.height / grid.length
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
    } yield {
      ()
    }
  }
}

object BoardType extends Enumeration {
  type BoardType = Value
  val Square, Triangular, Diamond = Value
}
