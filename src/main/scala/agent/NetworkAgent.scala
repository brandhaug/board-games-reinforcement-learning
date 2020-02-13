package agent

import environment.{Action, Environment}
import environment.ActionType.ActionType
import main.Arguments

case class NetworkAgent(initialEnvironment: Environment, epsilonRate: Double = Arguments.actorEpsilonRate) extends Agent {
  def act(environment: Environment): Action = {
    ???
  }

  def train(memories: List[Memory]): Agent = {
    ???
  }

  def updateEpsilonRate(): Agent = {
    ???
  }

  def removeEpsilon(): Agent = {
    ???
  }

  def resetEligibilities(): Agent = {
    ???
  }
}
