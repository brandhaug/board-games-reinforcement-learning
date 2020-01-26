package environment

object ActionType extends Enumeration {
  type ActionType = Value
  val Left: ActionType.Value = Value(0)
  val Right: ActionType.Value = Value(1)
  val Up: ActionType.Value = Value(2)
  val Down: ActionType.Value = Value(3)
}
