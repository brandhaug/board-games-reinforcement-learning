package environment.solo.pegsolitaire

import environment.EnvironmentType.EnvironmentType
import environment.{Action, BoardType, Environment, EnvironmentType}

import scala.collection.mutable

case class PegEnvironment(board: PegBoard) extends Environment {
  val environmentType: EnvironmentType = EnvironmentType.PegSolitaire
  val reward: Double = if (nonEmptyCells == 1) Double.MaxValue else Math.pow(board.grid.flatten.length - nonEmptyCells, 2)
  val possibleActions: List[Action] = {
    (for {
      (row, y)  <- board.grid.zipWithIndex
      (cell, x) <- row.zipWithIndex
    } yield {
      val possibleActions = mutable.Set[Action]()
      if (cell.isEmpty) {
        if (y < board.grid.length - 2 && board.grid(y + 1)(x).isNonEmpty && board.grid(y + 2)(x).isNonEmpty) {
          possibleActions += PegAction(x, y + 2, PegActionType.North)
        }

        if (board.boardType == BoardType.Diamond && y < board.grid.length - 2 && x > 1 && board.grid(y + 1)(x - 1).isNonEmpty && board.grid(y + 2)(x - 2).isNonEmpty) {
          possibleActions += PegAction(x - 2, y + 2, PegActionType.NorthEast)
        }

        if (x > 1 && row(x - 1).isNonEmpty && row(x - 2).isNonEmpty) {
          possibleActions += PegAction(x - 2, y, PegActionType.East)
        }

        if (board.boardType == BoardType.Triangular && y > 1 && x > 1 && board.grid(y - 1)(x - 1).isNonEmpty && board.grid(y - 2)(x - 2).isNonEmpty) {
          possibleActions += PegAction(x - 2, y - 2, PegActionType.SouthEast)
        }

        if (y > 1 && board.grid(y - 1)(x).isNonEmpty && board.grid(y - 2)(x).isNonEmpty) {
          possibleActions += PegAction(x, y - 2, PegActionType.South)
        }

        if (board.boardType == BoardType.Diamond && y > 1 && x < row.length - 2 && board.grid(y - 1)(x + 1).isNonEmpty && board.grid(y - 2)(x + 2).isNonEmpty) {
          possibleActions += PegAction(x + 2, y - 2, PegActionType.SouthWest)
        }

        if (x < row.length - 2 && row(x + 1).isNonEmpty && row(x + 2).isNonEmpty) {
          possibleActions += PegAction(x + 2, y, PegActionType.West)
        }

        if (board.boardType == BoardType.Triangular && y < board.grid.length - 2 && x < row.length - 2 && board.grid(y + 1)(x + 1).isNonEmpty && board.grid(y + 2)(x + 2).isNonEmpty) {
          possibleActions += PegAction(x + 2, y + 2, PegActionType.NorthWest)
        }
      }

      possibleActions
    }).flatten
  }

  def step(action: Action): Environment = {
    val newGrid = for {
      (gridRow, y) <- board.grid.zipWithIndex
      newGridRow = updateGridRowByAction(gridRow, y, action)
    } yield {
      newGridRow
    }

    val newBoard = PegBoard(newGrid, board.boardType)
    PegEnvironment(newBoard)
  }

  private def updateGridRowByAction(row: List[PegCell], y: Int, action: Action): List[PegCell] = {
    for {
      (cell, x) <- row.zipWithIndex
    } yield {
      if (action.xIndex == x && action.yIndex == y) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionId == PegActionType.North.id && action.yIndex == y + 2 && action.xIndex == x) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionId == PegActionType.North.id && action.yIndex == y + 1 && action.xIndex == x) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionId == PegActionType.NorthEast.id && action.yIndex == y + 2 && action.xIndex == x - 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionId == PegActionType.NorthEast.id && action.yIndex == y + 1 && action.xIndex == x - 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionId == PegActionType.East.id && action.yIndex == y && action.xIndex == x - 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionId == PegActionType.East.id && action.yIndex == y && action.xIndex == x - 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionId == PegActionType.SouthEast.id && action.yIndex == y - 2 && action.xIndex == x - 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionId == PegActionType.SouthEast.id && action.yIndex == y - 1 && action.xIndex == x - 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionId == PegActionType.South.id && action.yIndex == y - 2 && action.xIndex == x) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionId == PegActionType.South.id && action.yIndex == y - 1 && action.xIndex == x) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionId == PegActionType.SouthWest.id && action.yIndex == y - 2 && action.xIndex == x + 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionId == PegActionType.SouthWest.id && action.yIndex == y - 1 && action.xIndex == x + 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionId == PegActionType.West.id && action.yIndex == y && action.xIndex == x + 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionId == PegActionType.West.id && action.yIndex == y && action.xIndex == x + 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionId == PegActionType.NorthWest.id && action.yIndex == y + 2 && action.xIndex == x + 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionId == PegActionType.NorthWest.id && action.yIndex == y + 1 && action.xIndex == x + 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else cell
    }
  }

  def toggleCell(x: Int, y: Int): Environment = {
    val newGrid = for {
      gridRow <- board.grid
      newGridRow = updateGridRowByToggle(x, y, gridRow)
    } yield {
      newGridRow
    }

    val newBoard = PegBoard(newGrid, board.boardType)
    PegEnvironment(newBoard)
  }

  private def updateGridRowByToggle(x: Int, y: Int, row: List[PegCell]): List[PegCell] = {
    for {
      cell <- row
      cellStartX = board.cellStartX(row, cell.xIndex)
      cellStartY = cell.yIndex * board.cellHeight
    } yield {
      if (x > cellStartX && x < cellStartX + board.cellWidth && y > cellStartY && y < cellStartY + board.cellHeight) {
        PegCellType(cell.cellType) match {
          case PegCellType.Peg => PegCell(cell.xIndex, cell.yIndex, PegCellType.Empty, cell.boardType)
          case PegCellType.Empty => PegCell(cell.xIndex, cell.yIndex, PegCellType.None, cell.boardType)
          case PegCellType.None  => PegCell(cell.xIndex, cell.yIndex, PegCellType.Peg, cell.boardType)
        }
      } else {
        cell
      }
    }
  }

  override def toString: String = board.grid.flatten.map(_.cellType).mkString("")
}
