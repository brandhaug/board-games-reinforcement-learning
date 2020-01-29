package environment.pegsolitaire

import environment.ActionType.ActionType
import environment.{Action, ActionType, BoardType, Environment}
import scalafx.scene.canvas.GraphicsContext
import utils.ListUtils

import scala.collection.mutable

case class PegSolitaire(board: PegBoard) extends Environment {
  val pegsLeft: Int  = ListUtils.sumList(board.grid.map(_.count(_.isPeg)))
  val reward: Double = Math.pow(board.grid.flatten.length - pegsLeft, 2) // if (pegsLeft == 1) 1 else 0
  val possibleActions: List[Action] = {
    (for {
      (row, y)  <- board.grid.zipWithIndex
      (cell, x) <- row.zipWithIndex
    } yield {
      val possibleActions = mutable.Set[PegAction]()
      if (cell.isEmpty) {
        if (y < row.length - 2 && board.grid(y + 1)(x).isPeg && board.grid(y + 2)(x).isPeg) {
          possibleActions += PegAction(x, y + 2, ActionType.North)
        }

        if (board.boardType == BoardType.Diamond && y < row.length - 2 && x > 1 && board.grid(y + 1)(x - 1).isPeg && board.grid(y + 2)(x - 2).isPeg) {
          possibleActions += PegAction(x - 2, y + 2, ActionType.NorthEast)
        }

        if (x > 1 && row(x - 1).isPeg && row(x - 2).isPeg) {
          possibleActions += PegAction(x - 2, y, ActionType.East)
        }

        if (board.boardType == BoardType.Triangular && y > 1 && x > 1 && board.grid(y - 1)(x - 1).isPeg && board.grid(y - 2)(x - 2).isPeg) {
          possibleActions += PegAction(x - 2, y - 2, ActionType.SouthEast)
        }

        if (y > 1 && board.grid(y - 1)(x).isPeg && board.grid(y - 2)(x).isPeg) {
          possibleActions += PegAction(x, y - 2, ActionType.South)
        }

        if (board.boardType == BoardType.Diamond && y > 1 && x < row.length - 2 && board.grid(y - 1)(x + 1).isPeg && board.grid(y - 2)(x + 2).isPeg) {
          possibleActions += PegAction(x + 2, y - 2, ActionType.SouthWest)
        }

        if (x < row.length - 2 && row(x + 1).isPeg && row(x + 2).isPeg) {
          possibleActions += PegAction(x + 2, y, ActionType.West)
        }

        if (board.boardType == BoardType.Triangular && y < row.length - 2 && x < row.length - 2 && board.grid(y + 1)(x + 1).isPeg && board.grid(y + 2)(x + 2).isPeg) {
          possibleActions += PegAction(x + 2, y + 2, ActionType.NorthWest)
        }
      }
      possibleActions
    }).flatten
  }

  val isDone: Boolean = possibleActions.isEmpty

  val actionTypes: Set[ActionType] = {
    val defaultActionTypes = Set(ActionType.North, ActionType.East, ActionType.South, ActionType.West)
    board.boardType match {
      case BoardType.Square     => defaultActionTypes
      case BoardType.Triangular => defaultActionTypes ++ Set(ActionType.SouthEast, ActionType.NorthWest)
      case BoardType.Diamond    => defaultActionTypes ++ Set(ActionType.NorthEast, ActionType.SouthWest)
      case _                    => throw new Exception("Unknown board type")
    }
  }

  def step(action: Action): Environment = {
    val newGrid = for {
      (gridRow, y) <- board.grid.zipWithIndex
      newGridRow = updateGridRowByAction(gridRow, y, action)
    } yield {
      newGridRow
    }

    val newBoard = PegBoard(newGrid, board.boardType)
    PegSolitaire(newBoard)
  }

  def updateGridRowByAction(row: List[PegCell], y: Int, action: Action): List[PegCell] = {
    for {
      (cell, x) <- row.zipWithIndex
    } yield {
      if (action.x == x && action.y == y) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == ActionType.North && action.y - 2 == y && action.x == x) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == ActionType.North && action.y - 1 == y && action.x == x) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == ActionType.NorthEast && action.y - 2 == y && action.x == x - 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == ActionType.NorthEast && action.y - 1 == y && action.x == x - 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == ActionType.East && action.y == y && action.x + 2 == x) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == ActionType.East && action.y == y && action.x + 1 == x) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == ActionType.SouthEast && action.y + 2 == y && action.x == x + 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == ActionType.SouthEast && action.y + 1 == y && action.x == x + 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == ActionType.South && action.y + 2 == y && action.x == x) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == ActionType.South && action.y + 1 == y && action.x == x) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == ActionType.SouthWest && action.y + 2 == y && action.x == x - 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == ActionType.SouthWest && action.y + 1 == y && action.x == x - 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == ActionType.West && action.y == y && action.x - 2 == x) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == ActionType.West && action.y == y && action.x - 1 == x) PegCell(x, y, PegCellType.Empty, board.boardType)
      else if (action.actionType == ActionType.NorthWest && action.y - 2 == y && action.x == x + 2) PegCell(x, y, PegCellType.Peg, board.boardType)
      else if (action.actionType == ActionType.NorthWest && action.y - 1 == y && action.x == x + 1) PegCell(x, y, PegCellType.Empty, board.boardType)
      else cell
    }
  }

  def render(gc: GraphicsContext): Unit = {
    board.render(gc)
  }

  def toggleCell(x: Int, y: Int): Environment = {
    val newGrid = for {
      (gridRow, yIndex) <- board.grid.zipWithIndex
      newGridRow = updateGridRowByToggle(x, y, gridRow, yIndex)
    } yield {
      newGridRow
    }

    val newBoard = PegBoard(newGrid, board.boardType)
    PegSolitaire(newBoard)
  }

  def updateGridRowByToggle(x: Int, y: Int, row: List[PegCell], yIndex: Int): List[PegCell] = {
    for {
      (cell, xIndex) <- row.zipWithIndex
      cellStartX = board.cellStartX(row, xIndex)
      cellStartY = yIndex * board.cellHeight
    } yield {
      if (x > cellStartX && x < cellStartX + board.cellWidth && y > cellStartY && y < cellStartY + board.cellHeight) {
        cell.cellType match {
          case PegCellType.Peg   => PegCell(cell.xIndex, cell.yIndex, PegCellType.Empty, cell.boardType)
          case PegCellType.Empty => PegCell(cell.xIndex, cell.yIndex, PegCellType.None, cell.boardType)
          case PegCellType.None  => PegCell(cell.xIndex, cell.yIndex, PegCellType.Peg, cell.boardType)
        }
      } else {
        cell
      }
    }
  }

  override def toString: String = board.grid.flatten.map(_.cellType.id).mkString("")
}
