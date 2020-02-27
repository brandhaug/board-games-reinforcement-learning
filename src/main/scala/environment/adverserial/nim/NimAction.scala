package environment.adverserial.nim

import environment.Action

case class NimAction(xIndex: Int, yIndex: Int, actionId: Int) extends Action {}
