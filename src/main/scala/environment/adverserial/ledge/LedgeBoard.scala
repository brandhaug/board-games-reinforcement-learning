package environment.adverserial.ledge

import environment.{Board, BoardType, Cell}
import environment.BoardType.BoardType

case class LedgeBoard(grid: List[List[Cell]]) extends Board {
  val boardType: BoardType = BoardType.Square
}
