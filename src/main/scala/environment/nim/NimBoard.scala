package environment.nim

import environment.{Board, BoardType, Cell}
import environment.BoardType.BoardType

case class NimBoard (grid: List[List[Cell]]) extends Board {
  override val boardType: BoardType = BoardType.Square
}
