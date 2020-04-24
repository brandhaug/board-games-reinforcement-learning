package environment

import environment.BoardType.BoardType
import applications.mcts.Window
import scalafx.scene.canvas.GraphicsContext

trait Board {
  val grid: List[List[Cell]]
  val boardType: BoardType

  def size: Int = grid.size

  def cellWidth: Double = {
    boardType match {
      case BoardType.Hex => (Window.width - 40) / (grid.head.length.toDouble + ((grid.length - 1) * 0.55))
      case _             => Window.width / grid.head.length.toDouble
    }
  }

  def cellHeight: Double = boardType match {
    case BoardType.Hex => cellWidth
    case _             => Window.height / grid.length.toDouble
  }

  def render(gc: GraphicsContext): Unit = {
    for {
      row  <- grid
      cell <- row
      _ = boardType match {
        case BoardType.Hex =>
          cell.render(gc, cellWidth, cellHeight, grid.head.size - 1, grid.size - 1)
        case _ =>
          cell.render(
            gc,
            cellWidth * cell.xIndex,
            cellHeight * cell.yIndex,
            cellWidth,
            cellHeight
          )
      }
    } yield {
      ()
    }
  }
}

object BoardType extends Enumeration {
  type BoardType = Value
  val Square, Triangular, Diamond, Hex = Value
}
