package environment.pegsolitaire

import environment.enums.ActionType
import environment.{Action, Environment}
import environment.pegsolitaire.enums.PegCellType
import scalafx.scene.canvas.GraphicsContext

import scala.collection.mutable
import scala.util.Random

case class PegSolitaire(pegBoard: PegBoard) extends Environment {
  val reward: Int = 1

  val possibleActions: List[Action] = {
    (for {
      (line, y) <- pegBoard.grid.zipWithIndex
      (cell, x) <- line.zipWithIndex
    } yield {
      val possibleActions = mutable.Set[PegAction]()
      if (cell.isEmpty) {
        if (x > 1 && line(x - 1).isPeg && line(x - 2).isPeg) {
          possibleActions += PegAction(Random.nextLong().toString, x - 2, y, ActionType.Right)
        }

        if (x < line.length - 2 && line(x + 1).isPeg && line(x + 2).isPeg) {
          possibleActions += PegAction(Random.nextLong().toString, x + 2, y, ActionType.Left)
        }

        if (y > 1 && pegBoard.grid(y - 1)(x).isPeg && pegBoard.grid(y - 2)(x).isPeg) {
          possibleActions += PegAction(Random.nextLong().toString, x, y - 2, ActionType.Down)
        }

        if (y < line.length - 2 && pegBoard.grid(y + 1)(x).isPeg && pegBoard.grid(y + 2)(x).isPeg) {
          possibleActions += PegAction(Random.nextLong().toString, x, y + 2, ActionType.Up)
        }
      }
      possibleActions
    }).flatten
  }

  def step(action: Action): Environment = {
    val newGrid = for {
      (gridRow, y) <- pegBoard.grid.zipWithIndex
      newGridRow = updateGridRow(gridRow, y, action)
    } yield {
      newGridRow
    }

    val newBoard = PegBoard(newGrid, pegBoard.boardType)
    PegSolitaire(newBoard)
  }

  def updateGridRow(line: List[PegCell], y: Int, action: Action): List[PegCell] = {
    for {
      (cell, x) <- line.zipWithIndex
    } yield {
      if (action.x == x && action.y == y) PegCell(Random.nextLong().toString, x, y, PegCellType.Empty, pegBoard.boardType)
      else if (action.actionType == ActionType.Left && action.y == y && action.x - 2 == x) PegCell(Random.nextLong().toString, x, y, PegCellType.Peg, pegBoard.boardType)
      else if (action.actionType == ActionType.Right && action.y == y && action.x + 2 == x) PegCell(Random.nextLong().toString, x, y, PegCellType.Peg, pegBoard.boardType)
      else if (action.actionType == ActionType.Up && action.y - 2 == y && action.x == x) PegCell(Random.nextLong().toString, x, y, PegCellType.Peg, pegBoard.boardType)
      else if (action.actionType == ActionType.Down && action.y + 2 == y && action.x == x) PegCell(Random.nextLong().toString, x, y, PegCellType.Peg, pegBoard.boardType)
      else if (action.actionType == ActionType.Left && action.y == y && action.x - 1 == x) PegCell(Random.nextLong().toString, x, y, PegCellType.Empty, pegBoard.boardType)
      else if (action.actionType == ActionType.Right && action.y == y && action.x + 1 == x) PegCell(Random.nextLong().toString, x, y, PegCellType.Empty, pegBoard.boardType)
      else if (action.actionType == ActionType.Up && action.y - 1 == y && action.x == x) PegCell(Random.nextLong().toString, x, y, PegCellType.Empty, pegBoard.boardType)
      else if (action.actionType == ActionType.Down && action.y + 1 == y && action.x == x) PegCell(Random.nextLong().toString, x, y, PegCellType.Empty, pegBoard.boardType)
      else cell
    }
  }

  def render(gc: GraphicsContext): Unit = {
    pegBoard.render(gc)
  }
}
