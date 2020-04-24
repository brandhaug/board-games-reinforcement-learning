package environment.adversarial.hex

import environment.BoardType.BoardType
import environment.adversarial.hex.HexCellType.HexCellType
import environment.{BoardType, Cell}
import scalafx.scene.paint.Color

object HexCell {
  def apply(xIndex: Int, yIndex: Int, cellType: HexCellType): HexCell = {
    HexCell(xIndex, yIndex, cellType.id)
  }
}

case class HexCell(xIndex: Int, yIndex: Int, cellType: Int) extends Cell {
  val isEmpty: Boolean    = cellType == HexCellType.Empty.id
  val isNone: Boolean     = cellType == HexCellType.Empty.id
  val isNonEmpty: Boolean = cellType == HexCellType.Red.id || cellType == HexCellType.Blue.id

  val color: Color = {
    HexCellType(cellType) match {
      case HexCellType.Red   => Color.Red
      case HexCellType.Blue => Color.Blue
      case HexCellType.Empty  => Color.White
//      case HexCellType.None   => Color.Transparent
    }
  }

  val strokeColor: Color = {
    HexCellType(cellType) match {
      case HexCellType.Red   => Color.Black
      case HexCellType.Blue => Color.Black
      case HexCellType.Empty  => Color.Black
//      case HexCellType.None   => Color.Transparent
    }
  }

  val boardType: BoardType = BoardType.Hex

  override def toString: String = {
    cellType.toString
  }
}

object HexCellType extends Enumeration {
  type HexCellType = Value
  val Empty: HexCellType.Value  = Value(0)
  val Red: HexCellType.Value = Value(1)
  val Blue: HexCellType.Value   = Value(2)
}
