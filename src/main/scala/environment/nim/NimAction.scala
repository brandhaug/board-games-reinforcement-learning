package environment.nim

import environment.Action

case class NimAction(x: Int, y: Int, actionType: Int) extends Action {}
