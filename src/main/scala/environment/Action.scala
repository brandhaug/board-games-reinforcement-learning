package environment

trait Action {
  val x: Int
  val y: Int
  val actionType: Int
  def equals(otherAction: Action): Boolean = {
    x == otherAction.x && y == otherAction.y && actionType == otherAction.actionType
  }
}
