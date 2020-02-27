package environment.adverserial.ledge

import environment.Action

case class LedgeAction(xIndex: Int, yIndex: Int, actionId: Int) extends Action {}
