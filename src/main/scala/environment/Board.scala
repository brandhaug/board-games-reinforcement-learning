package environment

import environment.BoardType.BoardType
import applications.mcts.Window
import scalafx.scene.canvas.GraphicsContext

trait Board {
  val grid: List[List[Cell]]
  val boardType: BoardType

  def cellWidth: Double = boardType match {
    case BoardType.Hex => (Window.width / grid.head.length.toDouble) / 2
    case _             => Window.width / grid.head.length.toDouble
  }

  def cellHeight: Double = boardType match {
    case BoardType.Hex => (Window.width / grid.head.length.toDouble) / 2
    case _             => Window.height / grid.length.toDouble
  }

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
  val Square, Triangular, Diamond, Hex = Value
}
