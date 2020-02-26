package environment

trait Action {
  val xIndex: Int
  val yIndex: Int
  val actionId: Int
  def equals(otherAction: Action): Boolean = {
    xIndex == otherAction.xIndex && yIndex == otherAction.yIndex && actionId == otherAction.actionId
  }
}
