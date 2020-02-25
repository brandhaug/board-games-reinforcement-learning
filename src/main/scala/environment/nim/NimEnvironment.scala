package environment.nim

import environment.ActionType.ActionType
import environment.{Action, Cell, Environment}

case class NimEnvironment (board: NimBoard) extends Environment {
  val pegsLeft: Int = 1
  val reward: Double = 1
  val possibleActions: List[Action] = List()
  val isDone: Boolean = false
  val actionTypes: Set[ActionType] = Set()

  override def step(action: Action): Environment = {
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
    NimEnvironment(newBoard)
  }

  private def updateGridRowByToggle(x: Int, y: Int, row: List[NimCell], yIndex: Int): List[NimCell] = {
    for {
      (cell, xIndex) <- row.zipWithIndex
      cellStartX = xIndex * board.cellWidth
      cellStartY = yIndex * board.cellHeight
    } yield {
      if (x > cellStartX && x < cellStartX + board.cellWidth && y > cellStartY && y < cellStartY + board.cellHeight) {
        cell.cellType match {
          case NimCellType.Peg   => NimCell(cell.xIndex, cell.yIndex, NimCellType.Empty)
          case NimCellType.Empty => NimCell(cell.xIndex, cell.yIndex, NimCellType.Peg)
        }
      } else {
        cell
      }
    }
  }

  override def toString: String = board.grid.flatten.map(_.cellValue).mkString("")
}
