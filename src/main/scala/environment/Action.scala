package environment

import environment.pegsolitaire.PegCell

abstract class Action {
  val id: Int
  val from: PegCell
  val to: PegCell
  val over: PegCell
}
