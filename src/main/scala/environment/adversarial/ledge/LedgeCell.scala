package environment.adversarial.ledge

import environment.BoardType.BoardType
import environment.adversarial.ledge.LedgeCellType.LedgeCellType
import environment.{BoardType, Cell}
import scalafx.scene.paint.Color

object LedgeCell {
  def apply(xIndex: Int, yIndex: Int, cellType: LedgeCellType): LedgeCell = {
    LedgeCell(xIndex, yIndex, cellType.id)
  }
}

case class LedgeCell(xIndex: Int, yIndex: Int, cellType: Int) extends Cell {
  val isEmpty: Boolean    = cellType == LedgeCellType.Empty.id
  val isNone: Boolean     = cellType == LedgeCellType.None.id
  val isNonEmpty: Boolean = cellType == LedgeCellType.Copper.id || cellType == LedgeCellType.Gold.id

  val color: Color = {
    LedgeCellType(cellType) match {
      case LedgeCellType.Gold   => Color.Gold
      case LedgeCellType.Copper => Color.Chocolate
      case LedgeCellType.Empty  => Color.White
      case LedgeCellType.None   => Color.Transparent
    }
  }

  val strokeColor: Color = {
    LedgeCellType(cellType) match {
      case LedgeCellType.Gold   => Color.Black
      case LedgeCellType.Copper => Color.Black
      case LedgeCellType.Empty  => Color.Black
      case LedgeCellType.None   => Color.Transparent
    }
  }

  val boardType: BoardType = BoardType.Square

  override def toString: String = {
    LedgeCellType(cellType) match {
      case LedgeCellType.Gold   => "G"
      case LedgeCellType.Copper => "C"
      case LedgeCellType.Empty  => "O"
      case LedgeCellType.None   => ""
    }
  }
}

object LedgeCellType extends Enumeration {
  type LedgeCellType = Value
  val None: LedgeCellType.Value   = Value(0)
  val Empty: LedgeCellType.Value  = Value(1)
  val Copper: LedgeCellType.Value = Value(2)
  val Gold: LedgeCellType.Value   = Value(3)
}
