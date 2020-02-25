package environment.nim

import environment.{Board, BoardType}
import environment.BoardType.BoardType

case class NimBoard (grid: List[List[NimCell]]) extends Board {
  override val boardType: BoardType = BoardType.Square
}
