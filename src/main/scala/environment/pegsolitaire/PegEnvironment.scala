package environment.pegsolitaire

import environment.{Action, BoardType, Environment}
import utils.ListUtils

import scala.collection.mutable

case class PegEnvironment(board: PegBoard) extends Environment {
  val pegsLeft: Int  = ListUtils.sumList(board.grid.map(_.count(_.isPeg)))
  val reward: Double = if (pegsLeft == 1) Double.MaxValue else Math.pow(board.grid.flatten.length - pegsLeft, 2)
  val possibleActions: List[Action] = {
    (for {
      (row, y)  <- board.grid.zipWithIndex
      (cell, x) <- row.zipWithIndex
    } yield {
      val possibleActions = mutable.Set[Action]()
      if (cell.isEmpty) {
        if (y < board.grid.length - 2 && board.grid(y + 1)(x).isPeg && board.grid(y + 2)(x).isPeg) {
          possibleActions += PegAction(x, y + 2, PegActionType.North)
        }

        if (board.boardType == BoardType.Diamond && y < board.grid.length - 2 && x > 1 && board.grid(y + 1)(x - 1).isPeg && board.grid(y + 2)(x - 2).isPeg) {
          possibleActions += PegAction(x - 2, y + 2, PegActionType.NorthEast)
        }

        if (x > 1 && row(x - 1).isPeg && row(x - 2).isPeg) {
          possibleActions += PegAction(x - 2, y, PegActionType.East)
        }

        if (board.boardType == BoardType.Triangular && y > 1 && x > 1 && board.grid(y - 1)(x - 1).isPeg && board.grid(y - 2)(x - 2).isPeg) {
          possibleActions += PegAction(x - 2, y - 2, PegActionType.SouthEast)
        }

        if (y > 1 && board.grid(y - 1)(x).isPeg && board.grid(y - 2)(x).isPeg) {
          possibleActions += PegAction(x, y - 2, PegActionType.South)
        }

        if (board.boardType == BoardType.Diamond && y > 1 && x < row.length - 2 && board.grid(y - 1)(x + 1).isPeg && board.grid(y - 2)(x + 2).isPeg) {
          possibleActions += PegAction(x + 2, y - 2, PegActionType.SouthWest)
        }

        if (x < row.length - 2 && row(x + 1).isPeg && row(x + 2).isPeg) {
          possibleActions += PegAction(x + 2, y, PegActionType.West)
        }

        if (board.boardType == BoardType.Triangular && y < board.grid.length - 2 && x < row.length - 2 && board.grid(y + 1)(x + 1).isPeg && board.grid(y + 2)(x + 2).isPeg) {
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
      if (action.x == x && action.y == y) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == PegActionType.North.id && action.y == y + 2 && action.x == x) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == PegActionType.North.id && action.y == y + 1 && action.x == x) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == PegActionType.NorthEast.id && action.y == y + 2 && action.x == x - 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == PegActionType.NorthEast.id && action.y == y + 1 && action.x == x - 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == PegActionType.East.id && action.y == y && action.x == x - 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == PegActionType.East.id && action.y == y && action.x == x - 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == PegActionType.SouthEast.id && action.y == y - 2 && action.x == x - 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == PegActionType.SouthEast.id && action.y == y - 1 && action.x == x - 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == PegActionType.South.id && action.y == y - 2 && action.x == x) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == PegActionType.South.id && action.y == y - 1 && action.x == x) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == PegActionType.SouthWest.id && action.y == y - 2 && action.x == x + 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == PegActionType.SouthWest.id && action.y == y - 1 && action.x == x + 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == PegActionType.West.id && action.y == y && action.x == x + 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == PegActionType.West.id && action.y == y && action.x == x + 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == PegActionType.NorthWest.id && action.y == y + 2 && action.x == x + 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == PegActionType.NorthWest.id && action.y == y + 1 && action.x == x + 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else cell
    }
  }

  def toggleCell(x: Int, y: Int): Environment = {
    val newGrid = for {
      (gridRow, yIndex) <- board.grid.zipWithIndex
      newGridRow = updateGridRowByToggle(x, y, gridRow, yIndex)
    } yield {
      newGridRow
    }

    val newBoard = PegBoard(newGrid, board.boardType)
    PegEnvironment(newBoard)
  }

  private def updateGridRowByToggle(x: Int, y: Int, row: List[PegCell], yIndex: Int): List[PegCell] = {
    for {
      (cell, xIndex) <- row.zipWithIndex
      cellStartX = board.cellStartX(row, xIndex)
      cellStartY = yIndex * board.cellHeight
    } yield {
      if (x > cellStartX && x < cellStartX + board.cellWidth && y > cellStartY && y < cellStartY + board.cellHeight) {
        PegCellType(cell.cellType) match {
          case PegCellType.Peg => PegCell(cell.xIndex, cell.yIndex, PegCellType.Empty, cell.boardType)
          case PegCellType.Empty => PegCell(cell.xIndex, cell.yIndex, PegCellType.None, cell.boardType)
          case PegCellType.None  => PegCell(cell.xIndex, cell.yIndex, PegCellType.Peg, cell.boardType)
          case _ => throw new Exception("Unknown PegCellType")
        }
      } else {
        cell
      }
    }
  }

  override def toString: String = board.grid.flatten.map(_.cellType).mkString("")
}
