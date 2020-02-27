package environment.solo.pegsolitaire

import environment.BoardType.BoardType
import environment.Cell
import environment.solo.pegsolitaire.PegCellType.PegCellType
import scalafx.scene.paint.Color

object PegCell {
  def apply(xIndex: Int, yIndex: Int, cellType: PegCellType, boardType: BoardType): PegCell = {
    PegCell(xIndex, yIndex, cellType.id, boardType)
  }
}

case class PegCell(xIndex: Int, yIndex: Int, cellType: Int, boardType: BoardType) extends Cell {
  val isEmpty: Boolean = cellType == PegCellType.Empty.id
  val isNone: Boolean = cellType == PegCellType.None.id
  val isNonEmpty: Boolean = cellType == PegCellType.Peg.id

  val color: Color = {
    PegCellType(cellType) match {
      case PegCellType.Peg   => Color.Red
      case PegCellType.Empty => Color.White
      case PegCellType.None  => Color.Transparent
    }
  }

  val strokeColor: Color = {
    PegCellType(cellType) match {
      case PegCellType.Peg   => Color.Black
      case PegCellType.Empty => Color.Black
      case PegCellType.None  => Color.Transparent
    }
  }
}

object PegCellType extends Enumeration {
  type PegCellType = Value
  val None: PegCellType.Value = Value(0)
  val Peg: PegCellType.Value   = Value(1)
  val Empty: PegCellType.Value = Value(2)
}
