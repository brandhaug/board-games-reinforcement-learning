package environment.pegsolitaire

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
          possibleActions += PegAction(Random.nextInt, line(x - 2), line(x), line(x - 1))
        }

        if (x < line.length - 3 && line(x + 1).isPeg && line(x + 2).isPeg) {
          possibleActions += PegAction(Random.nextInt, line(x + 2), line(x), line(x + 1))
        }

        if (y > 1 && pegBoard.grid(y - 1)(x).isPeg && pegBoard.grid(y - 2)(x).isPeg) {
          possibleActions += PegAction(Random.nextInt, pegBoard.grid(y - 2)(x), pegBoard.grid(y)(x), pegBoard.grid(y - 1)(x))
        }

        if (y < line.length - 3 && pegBoard.grid(y + 1)(x).isPeg && pegBoard.grid(y + 2)(x).isPeg) {
          possibleActions += PegAction(Random.nextInt, pegBoard.grid(y + 2)(x), pegBoard.grid(y)(x), pegBoard.grid(y + 1)(x))
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
      cell <- line
    } yield {
      cell.id match {
        case action.from.id => PegCell(cell.id, cell.x, cell.y, PegCellType.Empty, pegBoard.boardType)
        case action.to.id   => PegCell(cell.id, cell.x, cell.y, PegCellType.Peg, pegBoard.boardType)
        case action.over.id => PegCell(cell.id, cell.x, cell.y, PegCellType.Empty, pegBoard.boardType)
        case _ => cell
      }
    }
  }

  def render(gc: GraphicsContext): Unit = {
    pegBoard.render(gc)
  }
}
