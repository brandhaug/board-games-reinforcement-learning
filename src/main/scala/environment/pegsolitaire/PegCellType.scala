package environment.pegsolitaire

object PegCellType extends Enumeration {
  type PegCellType = Value
  val None: PegCellType.Value = Value(0)
  val Peg: PegCellType.Value   = Value(1)
  val Empty: PegCellType.Value = Value(2)
}
