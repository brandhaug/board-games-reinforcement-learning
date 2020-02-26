package environment.pegsolitaire
import environment.Action
import environment.pegsolitaire.PegActionType.PegActionType

object PegAction {
  def apply(x: Int, y: Int, actionType: PegActionType): PegAction = {
    PegAction(x, y, actionType.id)
  }
}

case class PegAction(x: Int, y: Int, actionType: Int) extends Action {

}

object PegActionType extends Enumeration {
  type PegActionType = Value
  val North: PegActionType.Value = Value(0)
  val NorthEast: PegActionType.Value = Value(1)
  val East: PegActionType.Value = Value(2)
  val SouthEast: PegActionType.Value = Value(3)
  val South: PegActionType.Value = Value(4)
  val SouthWest: PegActionType.Value = Value(5)
  val West: PegActionType.Value = Value(6)
  val NorthWest: PegActionType.Value = Value(7)
}
