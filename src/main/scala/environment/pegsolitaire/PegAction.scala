package environment.pegsolitaire

import environment.Action
import environment.enums.ActionType.ActionType

case class PegAction(id: String, x: Int, y: Int, actionType: ActionType) extends Action {

}
