package environment

object ActionType extends Enumeration {
  type ActionType = Value
  val North: ActionType.Value = Value(0)
  val NorthEast: ActionType.Value = Value(1)
  val East: ActionType.Value = Value(2)
  val SouthEast: ActionType.Value = Value(3)
  val South: ActionType.Value = Value(4)
  val SouthWest: ActionType.Value = Value(5)
  val West: ActionType.Value = Value(6)
  val NorthWest: ActionType.Value = Value(7)
}
