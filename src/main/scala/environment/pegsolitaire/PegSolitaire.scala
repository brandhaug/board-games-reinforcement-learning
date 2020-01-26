package environment.pegsolitaire

import environment.ActionType.ActionType
import environment.{Action, ActionType, BoardType, Environment}
import scalafx.scene.canvas.GraphicsContext
import utils.ListUtils

import scala.collection.mutable

case class PegSolitaire(board: PegBoard) extends Environment {
  val pegsLeft: Int  = ListUtils.sumList(board.grid.map(_.count(_.isPeg)))
  val reward: Double = Math.pow(board.grid.flatten.length - pegsLeft, 2)
  val possibleActions: List[Action] = {
    (for {
      (line, y) <- board.grid.zipWithIndex
      (cell, x) <- line.zipWithIndex
    } yield {
      val possibleActions = mutable.Set[PegAction]()
      if (cell.isEmpty) {
        if (y < line.length - 2 && board.grid(y + 1)(x).isPeg && board.grid(y + 2)(x).isPeg) {
          possibleActions += PegAction(x, y + 2, ActionType.North)
        }

        if (board.boardType == BoardType.Diamond && y < line.length - 2 && x > 1 && board.grid(y + 1)(x - 1).isPeg && board.grid(y + 2)(x - 2).isPeg) {
          possibleActions += PegAction(x, y + 2, ActionType.NorthEast)
        }

        if (x > 1 && line(x - 1).isPeg && line(x - 2).isPeg) {
          possibleActions += PegAction(x - 2, y, ActionType.East)
        }

        if (board.boardType == BoardType.Triangular && y > 1 && x > 1 && board.grid(y - 1)(x - 1).isPeg && board.grid(y - 2)(x - 2).isPeg) {
          possibleActions += PegAction(x, y - 2, ActionType.SouthEast)
        }

        if (y > 1 && board.grid(y - 1)(x).isPeg && board.grid(y - 2)(x).isPeg) {
          possibleActions += PegAction(x, y - 2, ActionType.South)
        }

        if (board.boardType == BoardType.Diamond && y > 1 && x < line.length - 2 && board.grid(y - 1)(x + 1).isPeg && board.grid(y - 2)(x + 2).isPeg) {
          possibleActions += PegAction(x, y - 2, ActionType.SouthWest)
        }

        if (x < line.length - 2 && line(x + 1).isPeg && line(x + 2).isPeg) {
          possibleActions += PegAction(x + 2, y, ActionType.West)
        }

        if (board.boardType == BoardType.Triangular && y < line.length - 2 && x < line.length - 2 && board.grid(y + 1)(x + 1).isPeg && board.grid(y + 2)(x + 2).isPeg) {
          possibleActions += PegAction(x, y + 2, ActionType.NorthWest)
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
      newGridRow = updateGridRow(gridRow, y, action)
    } yield {
      newGridRow
    }

    val newBoard = PegBoard(newGrid, board.boardType)
    PegSolitaire(newBoard)
  }

  def updateGridRow(line: List[PegCell], y: Int, action: Action): List[PegCell] = {
    for {
      (cell, x) <- line.zipWithIndex
    } yield {
      if (action.x == x && action.y == y) PegCell(x, y, PegCellType.Empty, board.boardType)

      action.actionType match {
        case ActionType.North =>
          if (action.y - 2 == y && action.x == x) PegCell(x, y, PegCellType.Peg, board.boardType)
          else if (action.y - 1 == y && action.x == x) PegCell(x, y, PegCellType.Empty, board.boardType)
          else cell
        case ActionType.NorthEast =>
          if (action.y - 2 == y && action.x == x - 2) PegCell(x, y, PegCellType.Peg, board.boardType)
          else if (action.y - 1 == y && action.x == x - 1) PegCell(x, y, PegCellType.Empty, board.boardType)
          else cell
        case ActionType.East =>
          if (action.y == y && action.x + 2 == x) PegCell(x, y, PegCellType.Peg, board.boardType)
          else if (action.y == y && action.x + 1 == x) PegCell(x, y, PegCellType.Empty, board.boardType)
          else cell
        case ActionType.SouthEast =>
          if (action.y + 2 == y && action.x == x + 2) PegCell(x, y, PegCellType.Peg, board.boardType)
          else if (action.y + 1 == y && action.x == x + 1) PegCell(x, y, PegCellType.Empty, board.boardType)
          else cell
        case ActionType.South =>
          if (action.y + 2 == y && action.x == x) PegCell(x, y, PegCellType.Peg, board.boardType)
          else if (action.y + 1 == y && action.x == x) PegCell(x, y, PegCellType.Empty, board.boardType)
          else cell
        case ActionType.SouthWest =>
          if (action.y + 2 == y && action.x == x - 2) PegCell(x, y, PegCellType.Peg, board.boardType)
          else if (action.y + 1 == y && action.x == x - 1) PegCell(x, y, PegCellType.Empty, board.boardType)
          else cell
        case ActionType.West =>
          if (action.y == y && action.x - 2 == x) PegCell(x, y, PegCellType.Peg, board.boardType)
          else if (action.y == y && action.x - 1 == x) PegCell(x, y, PegCellType.Empty, board.boardType)
          else cell
        case ActionType.NorthWest =>
          if (action.y - 2 == y && action.x == x + 2) PegCell(x, y, PegCellType.Peg, board.boardType)
          else if (action.y - 1 == y && action.x == x + 1) PegCell(x, y, PegCellType.Empty, board.boardType)
          else cell
        case _ =>
          throw new Exception("Unknown ActionType")
      }
    }
  }

  def render(gc: GraphicsContext): Unit = {
    board.render(gc)
  }

  override def toString: String = board.grid.flatten.map(_.cellType.id).mkString("")
}
