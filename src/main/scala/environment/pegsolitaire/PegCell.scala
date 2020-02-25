package environment.pegsolitaire

import environment.BoardType.BoardType
import environment.{BoardType, Cell}
import environment.pegsolitaire.PegCellType.PegCellType
import scalafx.scene.paint.Color

case class PegCell(xIndex: Int, yIndex: Int, cellType: PegCellType, boardType: BoardType) extends Cell {
  val isEmpty: Boolean = cellType == PegCellType.Empty
  val isNone: Boolean = cellType == PegCellType.None
  val isPeg: Boolean = cellType == PegCellType.Peg

  val color: Color = {
    cellType match {
      case PegCellType.Peg   => Color.Red
      case PegCellType.Empty => Color.White
      case PegCellType.None  => Color.Transparent
    }
  }

  val strokeColor: Color = {
    cellType match {
      case PegCellType.Peg   => Color.Black
      case PegCellType.Empty => Color.Black
      case PegCellType.None  => Color.Transparent
    }
  }

  val cellValue: Int = cellType.id
}
