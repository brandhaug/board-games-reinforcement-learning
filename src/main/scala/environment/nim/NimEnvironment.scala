package environment.nim

import environment.{Action, Board, Cell, Environment}

case class NimEnvironment(board: Board, maxTake: Int) extends Environment {
  val reward: Double = if (nonEmptyCells == 0) 100 else 0
  val possibleActions: List[Action] = {
    val limit = if (nonEmptyCells > maxTake) maxTake else nonEmptyCells
    (1 to limit).toList.map(take => NimAction(0, 0, take))
  }

  def step(action: Action): Environment = {
    val take          = action.actionType
    val newEmptyCells = emptyCells + take
    NimEnvironmentCreator.createEnvironment(nonEmptyCells + emptyCells, maxTake, emptyCells = newEmptyCells)
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
          case NimCellType.None  => NimCell(cell.xIndex, cell.yIndex, NimCellType.None)
        }
      } else {
        cell
      }
    }
  }

  override def toString: String = board.grid.flatten.map(_.cellType).mkString("")
}
