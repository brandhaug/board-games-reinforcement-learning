package agent

import environment.{Action, Environment}
import environment.ActionType.ActionType

case class NetworkAgent(initialEnvironment: Environment) extends Agent {
  def act(environment: Environment): Action = {
    ???
  }

  def train(memories: List[Memory]): Agent = {
    ???
  }

  def updateRates(): Agent = {
    ???
  }
}
