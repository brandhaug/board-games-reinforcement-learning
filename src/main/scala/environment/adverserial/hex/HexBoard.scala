package environment.adverserial.hex

import environment.BoardType.BoardType
import environment.{Board, BoardType, Cell}

case class HexBoard(grid: List[List[Cell]]) extends Board {
  val boardType: BoardType = BoardType.Diamond
}
