package environment.adversarial.hex

import environment.BoardType.BoardType
import environment.{Board, BoardType, Cell}

case class HexBoard(grid: List[List[Cell]]) extends Board {
  val boardType: BoardType = BoardType.Hex
  val redCellsCount: Int = grid.flatten.count(_.cellType == HexCellType.Red.id)
  val blueCellsCount: Int = grid.flatten.count(_.cellType == HexCellType.Blue.id)
}
