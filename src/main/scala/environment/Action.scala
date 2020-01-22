package environment

import environment.enums.ActionType.ActionType

abstract class Action {
  val id: String
  val x: Int
  val y: Int
  val actionType: ActionType
}
