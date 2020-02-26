package environment.ledge

import environment.{Action, Board, Cell, Environment}

import scala.collection.mutable

case class LedgeEnvironment(board: Board) extends Environment {
  val reward: Double = {
    val goldExists = board.grid.flatten.exists(_.cellType == LedgeCellType.Gold.id)
    if (goldExists) 0 else 100
  }
  val possibleActions: List[Action] = {
    val flattenedGrid = board.grid.flatten

    (for {
      (cell, index) <- flattenedGrid.zipWithIndex
    } yield {
      val possibleActions = mutable.Set[Action]()
      if (index == 0 && cell.isNonEmpty) possibleActions += LedgeAction(0, 0, 0)
      else if (cell.isEmpty) {
        val nextNonEmptyCellOption = flattenedGrid.find(cell2 => flattenedGrid.indexOf(cell2) > index && cell2.isNonEmpty)

        if (nextNonEmptyCellOption.nonEmpty) {
          val nextNonEmptyCell = nextNonEmptyCellOption.get
          possibleActions += LedgeAction(nextNonEmptyCell.xIndex, nextNonEmptyCell.yIndex, index)
        }
      }

      possibleActions
    }).flatten
  }

  def step(action: Action): Environment = {
    val newGrid = for {
      gridRow <- board.grid
      newGridRow = updateGridRowByAction(action, gridRow)
    } yield {
      newGridRow
    }

    val newBoard = LedgeBoard(newGrid)
    LedgeEnvironment(newBoard)
  }

  private def updateGridRowByAction(action: Action, row: List[Cell]): List[Cell] = {
    for {
      cell <- row
      cellIndex = (cell.yIndex * board.grid.size) + cell.xIndex
    } yield {
      if (cellIndex == action.actionId) {
        val newCellType = board.grid(action.yIndex)(action.xIndex).cellType
        LedgeCell(cell.xIndex, cell.yIndex, newCellType)
      } else if (cell.xIndex == action.xIndex && cell.yIndex == action.yIndex) {
        LedgeCell(cell.xIndex, cell.yIndex, LedgeCellType.Empty)
      } else {
        cell
      }
    }
  }

  def toggleCell(x: Int, y: Int): Environment = {
    val newGrid = for {
      gridRow <- board.grid
      newGridRow = updateGridRowByToggle(x, y, gridRow)
    } yield {
      newGridRow
    }

    val newBoard = LedgeBoard(newGrid)
    LedgeEnvironment(newBoard)
  }

  private def updateGridRowByToggle(x: Int, y: Int, row: List[Cell]): List[Cell] = {
    for {
      cell <- row
      cellStartX = cell.xIndex * board.cellWidth
      cellStartY = cell.yIndex * board.cellHeight
    } yield {
      if (x > cellStartX && x < cellStartX + board.cellWidth && y > cellStartY && y < cellStartY + board.cellHeight) {
        LedgeCellType(cell.cellType) match {
          case LedgeCellType.Copper => LedgeCell(cell.xIndex, cell.yIndex, LedgeCellType.Empty)
          case LedgeCellType.Empty  => LedgeCell(cell.xIndex, cell.yIndex, LedgeCellType.Copper)
          case _                    => cell
        }
      } else {
        cell
      }
    }
  }

  override def toString: String = board.grid.flatten.map(_.cellType).mkString("")
}
