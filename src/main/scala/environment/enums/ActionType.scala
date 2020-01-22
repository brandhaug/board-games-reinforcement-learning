package environment.enums

object ActionType extends Enumeration {
  type ActionType = Value
  val Left = Value(0)
  val Right = Value(1)
  val Up = Value(2)
  val Down = Value(3)
}
