package environment.adverserial.hex

import environment.EnvironmentType.EnvironmentType
import environment._

case class HexEnvironment(board: Board) extends Environment {
  val environmentType: EnvironmentType = EnvironmentType.Hex
  val reward: Double = {
    ???
  }
  val possibleActions: List[Action] = {
    ???
  }

  def step(action: Action): Environment = {
    ???
  }

  private def updateGridRowByAction(action: Action, row: List[Cell]): List[Cell] = {
    ???
  }

  def toggleCell(x: Int, y: Int): Environment = {
    ???
  }

  private def updateGridRowByToggle(x: Int, y: Int, row: List[Cell]): List[Cell] = {
    ???
  }

  override def toString: String = board.grid.flatten.map(_.toString).mkString("")
}
