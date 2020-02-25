package environment.nim

import environment.BoardType.BoardType
import environment.nim.NimCellType.NimCellType
import environment.{BoardType, Cell}
import scalafx.scene.paint.Color

case class NimCell (xIndex: Int, yIndex: Int, cellType: NimCellType) extends Cell {
  val color: Color = {
    cellType match {
      case NimCellType.Peg   => Color.Red
      case NimCellType.Empty => Color.White
      case NimCellType.None  => Color.Transparent
    }
  }

  val strokeColor: Color = {
    cellType match {
      case NimCellType.Peg   => Color.Black
      case NimCellType.Empty => Color.Black
      case NimCellType.None  => Color.Transparent
    }
  }

  val boardType: BoardType = BoardType.Square
  val cellValue: Int = cellType.id
}
