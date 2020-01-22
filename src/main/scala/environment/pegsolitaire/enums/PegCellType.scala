package environment.pegsolitaire.enums

import environment.pegsolitaire.enums

object PegCellType extends Enumeration {
  type PegCellType = Value
  val None = Value(0)
  val Peg   = Value(1)
  val Empty = Value(2)

}
