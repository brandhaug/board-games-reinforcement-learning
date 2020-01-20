package environment.pegsolitaire

import environment.Action

case class PegAction(id: Int, from: PegCell, to: PegCell, over: PegCell) extends Action {

}
