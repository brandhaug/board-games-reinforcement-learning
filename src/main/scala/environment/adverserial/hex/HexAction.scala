package environment.adverserial.hex

import environment.Action

case class HexAction(xIndex: Int, yIndex: Int, actionId: Int) extends Action {}
