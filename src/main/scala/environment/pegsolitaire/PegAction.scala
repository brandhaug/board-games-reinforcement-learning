package environment.pegsolitaire

import environment.Action
import environment.ActionType.ActionType

case class PegAction(x: Int, y: Int, actionType: ActionType) extends Action {

}
