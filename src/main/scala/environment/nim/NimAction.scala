package environment.nim

import environment.Action

case class NimAction(xIndex: Int, yIndex: Int, actionId: Int) extends Action {}
