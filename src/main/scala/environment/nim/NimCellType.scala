package environment.nim

object NimCellType extends Enumeration {
  type NimCellType = Value
  val None: NimCellType.Value = Value(0)
  val Empty: NimCellType.Value = Value(1)
  val Peg: NimCellType.Value   = Value(2)
}
