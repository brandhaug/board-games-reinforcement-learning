package environment.nim

import environment.BoardType.BoardType
import environment.nim.NimCellType.NimCellType
import environment.{BoardType, Cell}
import scalafx.scene.paint.Color

object NimCell {
  def apply(xIndex: Int, yIndex: Int, cellType: NimCellType): NimCell = {
    NimCell(xIndex, yIndex, cellType.id)
  }
}

case class NimCell(xIndex: Int, yIndex: Int, cellType: Int) extends Cell {
  val isEmpty: Boolean = cellType == NimCellType.Empty.id
  val isNone: Boolean  = cellType == NimCellType.None.id
  val isPeg: Boolean   = cellType == NimCellType.Peg.id

  val color: Color = {
    NimCellType(cellType) match {
      case NimCellType.Peg   => Color.Red
      case NimCellType.Empty => Color.White
      case NimCellType.None  => Color.Transparent
    }
  }

  val strokeColor: Color = {
    NimCellType(cellType) match {
      case NimCellType.Peg   => Color.Black
      case NimCellType.Empty => Color.Black
      case NimCellType.None  => Color.Transparent
    }
  }

  val boardType: BoardType = BoardType.Square
}

object NimCellType extends Enumeration {
  type NimCellType = Value
  val None: NimCellType.Value  = Value(0)
  val Empty: NimCellType.Value = Value(1)
  val Peg: NimCellType.Value   = Value(2)
}
