package environment

import ActionType.ActionType

abstract class Action {
  val x: Int
  val y: Int
  val actionType: ActionType
  def equals(otherAction: Action): Boolean = {
    x == otherAction.x && y == otherAction.y && actionType.id == otherAction.actionType.id
  }
}
