package environment.nim

import environment.{Action, Board, Cell, Environment}
import utils.ListUtils

case class NimEnvironment(board: Board, maxTake: Int) extends Environment {
  val pegsLeft: Int  = ListUtils.sumList(board.grid.map(_.count(_.cellType == NimCellType.Peg.id)))
  val reward: Double = if (pegsLeft == 0) 100 else 0
  val possibleActions: List[Action] = {
    val limit = if (pegsLeft > maxTake) maxTake else pegsLeft
    (1 to limit).toList.map(take => NimAction(0, 0, take))
  }

  def step(action: Action): Environment = {
    ???
  }

  def toggleCell(x: Int, y: Int): Environment = {
    val newGrid = for {
      (gridRow, yIndex) <- board.grid.zipWithIndex
      newGridRow = updateGridRowByToggle(x, y, gridRow, yIndex)
    } yield {
      newGridRow
    }

    val newBoard = NimBoard(newGrid)
    NimEnvironment(newBoard, maxTake)
  }

  private def updateGridRowByToggle(x: Int, y: Int, row: List[Cell], yIndex: Int): List[Cell] = {
    for {
      (cell, xIndex) <- row.zipWithIndex
      cellStartX = xIndex * board.cellWidth
      cellStartY = yIndex * board.cellHeight
    } yield {
      if (x > cellStartX && x < cellStartX + board.cellWidth && y > cellStartY && y < cellStartY + board.cellHeight) {
        NimCellType(cell.cellType) match {
          case NimCellType.Peg   => NimCell(cell.xIndex, cell.yIndex, NimCellType.Empty)
          case NimCellType.Empty => NimCell(cell.xIndex, cell.yIndex, NimCellType.Peg)
          case NimCellType.None => NimCell(cell.xIndex, cell.yIndex, NimCellType.None)
        }
      } else {
        cell
      }
    }
  }

  override def toString: String = board.grid.flatten.map(_.cellType).mkString("")
}
