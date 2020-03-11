package environment.solo.pegsolitaire

import environment.{Board, BoardType}
import environment.BoardType.BoardType
import scalafx.scene.canvas.GraphicsContext

case class PegBoard(grid: List[List[PegCell]], boardType: BoardType) extends Board {
  override def render(gc: GraphicsContext): Unit = {
    for {
      row  <- grid
      cell <- row
      _ = cell.render(
        gc,
        cellStartX(row, cell.xIndex),
        cellHeight * cell.yIndex,
        cellWidth,
        cellHeight
      )
    } yield {
      ()
    }
  }

  def cellStartX(line: List[PegCell], xIndex: Int): Double = {
    val baseStartX = cellWidth * xIndex
    boardType match {
      case BoardType.Square => baseStartX
      case BoardType.Triangular =>
        val noneCells = line.count(_.isNone)
        baseStartX + (noneCells * (cellWidth / 2))
      case BoardType.Diamond => baseStartX
      case _                 => throw new Exception("Unknown BoardType")
    }
  }
}
