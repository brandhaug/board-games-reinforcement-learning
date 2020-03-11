package environment.adversarial.hex

import environment.EnvironmentType.EnvironmentType
import environment._

case class HexEnvironment(board: HexBoard) extends Environment {
  val environmentType: EnvironmentType = EnvironmentType.Hex
  val reward: Double = {
    0.0
  }

  val possibleActions: List[Action] = {
    for {
      row       <- board.grid
      cell      <- row
      emptyCell = cell if cell.isEmpty
    } yield {
      HexAction(emptyCell.xIndex, emptyCell.yIndex, 0)
    }
  }

  def step(action: Action): Environment = {
    val newGrid = for {
      gridRow <- board.grid
      newGridRow = updateGridRowByAction(action, gridRow)
    } yield {
      newGridRow
    }

    val newBoard = HexBoard(newGrid)
    HexEnvironment(newBoard)
  }

  private def updateGridRowByAction(action: Action, row: List[Cell]): List[Cell] = {
    for {
      cell <- row
    } yield {
      if (cell.xIndex == action.xIndex && cell.yIndex == action.yIndex) {
        val cellType = if (board.redCellsCount == board.blueCellsCount) HexCellType.Red else HexCellType.Blue
        HexCell(cell.xIndex, cell.yIndex, cellType)
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

    val newBoard = HexBoard(newGrid)
    HexEnvironment(newBoard)
  }

  private def updateGridRowByToggle(x: Int, y: Int, row: List[Cell]): List[Cell] = {
    for {
      cell <- row
      cellStartX = cell.xIndex * board.cellWidth
      cellStartY = cell.yIndex * board.cellHeight
    } yield {
      if (x > cellStartX && x < cellStartX + board.cellWidth && y > cellStartY && y < cellStartY + board.cellHeight) {
        HexCellType(cell.cellType) match {
          case HexCellType.Red   => HexCell(cell.xIndex, cell.yIndex, HexCellType.Blue)
          case HexCellType.Blue  => HexCell(cell.xIndex, cell.yIndex, HexCellType.Empty)
          case HexCellType.Empty => HexCell(cell.xIndex, cell.yIndex, HexCellType.Red)
          case _                 => cell
        }
      } else {
        cell
      }
    }
  }

  override def toString: String = board.grid.flatten.map(_.toString).mkString("")
}
