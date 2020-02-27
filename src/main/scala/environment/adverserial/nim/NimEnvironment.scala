package environment.adverserial.nim

import environment.EnvironmentType.EnvironmentType
import environment.{Action, Board, Cell, Environment, EnvironmentType}

case class NimEnvironment(board: Board, maxTake: Int) extends Environment {
  val environmentType: EnvironmentType = EnvironmentType.Nim
  val reward: Double = if (nonEmptyCells == 0) 100 else 0
  val possibleActions: List[Action] = {
    val limit = if (nonEmptyCells > maxTake) maxTake else nonEmptyCells
    (1 to limit).toList.map(take => NimAction(0, 0, take))
  }

  def step(action: Action): Environment = {
    val take          = action.actionId
    val newEmptyCells = emptyCells + take
    NimEnvironmentCreator.createEnvironment(nonEmptyCells + emptyCells, maxTake, emptyCells = newEmptyCells)
  }

  def toggleCell(x: Int, y: Int): Environment = {
    val newGrid = for {
      gridRow <- board.grid
      newGridRow = updateGridRowByToggle(x, y, gridRow)
    } yield {
      newGridRow
    }

    val newBoard = NimBoard(newGrid)
    NimEnvironment(newBoard, maxTake)
  }

  private def updateGridRowByToggle(x: Int, y: Int, row: List[Cell]): List[Cell] = {
    for {
      cell <- row
      cellStartX = cell.xIndex * board.cellWidth
      cellStartY = cell.yIndex * board.cellHeight
    } yield {
      if (x > cellStartX && x < cellStartX + board.cellWidth && y > cellStartY && y < cellStartY + board.cellHeight) {
        NimCellType(cell.cellType) match {
          case NimCellType.Peg   => NimCell(cell.xIndex, cell.yIndex, NimCellType.Empty)
          case NimCellType.Empty => NimCell(cell.xIndex, cell.yIndex, NimCellType.Peg)
          case _  => cell
        }
      } else {
        cell
      }
    }
  }

  override def toString: String = board.grid.flatten.map(_.cellType).mkString("")
}
