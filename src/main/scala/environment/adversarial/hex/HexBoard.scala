package environment.adversarial.hex

import environment.BoardType.BoardType
import environment.{Board, BoardType, Cell}

import scala.collection.mutable

case class HexBoard(grid: List[List[Cell]]) extends Board {
  val boardType: BoardType = BoardType.Hex
  val redCellsCount: Int = grid.flatten.count(_.cellType == HexCellType.Red.id)
  val blueCellsCount: Int = grid.flatten.count(_.cellType == HexCellType.Blue.id)

  def neighbors(cell: Cell): Set[Cell] = {
    val neighboringCells = mutable.Set[Cell]()

    if (cell.yIndex > 0 && grid(cell.yIndex - 1)(cell.xIndex).cellType == cell.cellType) { // North
      neighboringCells += grid(cell.yIndex - 1)(cell.xIndex)
    }

    if (cell.yIndex > 0 && cell.xIndex < grid.head.length - 1 && grid(cell.yIndex - 1)(cell.xIndex + 1).cellType == cell.cellType) { // NorthEast
      neighboringCells += grid(cell.yIndex - 1)(cell.xIndex + 1)
    }

    if (cell.xIndex < grid.head.length - 1 && grid(cell.yIndex)(cell.xIndex + 1).cellType == cell.cellType) { // East
      neighboringCells += grid(cell.yIndex)(cell.xIndex + 1)
    }

    if (cell.yIndex < grid.length - 1 && grid(cell.yIndex + 1)(cell.xIndex).cellType == cell.cellType) { // South
      neighboringCells += grid(cell.yIndex + 1)(cell.xIndex)
    }

    if (cell.yIndex < grid.length - 1 && cell.xIndex > 0 && grid(cell.yIndex + 1)(cell.xIndex - 1).cellType == cell.cellType) { // SouthWest
      neighboringCells += grid(cell.yIndex + 1)(cell.xIndex - 1)
    }

    if (cell.xIndex > 0 && grid(cell.yIndex)(cell.xIndex - 1).cellType == cell.cellType) { // West
      neighboringCells += grid(cell.yIndex)(cell.xIndex - 1)
    }





    neighboringCells.toSet
  }

}
