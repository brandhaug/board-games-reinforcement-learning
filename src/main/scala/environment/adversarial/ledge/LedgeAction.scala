package environment.adversarial.ledge

import environment.Action

case class LedgeAction(xIndex: Int, yIndex: Int, actionId: Int) extends Action {}
