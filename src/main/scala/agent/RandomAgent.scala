package agent

import environment.{Action, Environment}
import environment.ActionType.ActionType
import main.Arguments

import scala.util.Random

case class RandomAgent(initialEnvironment: Environment, epsilonRate: Double = Arguments.actorEpsilonRate) extends Agent {
  def act(environment: Environment): Action = {
    randomAction(environment)
  }

  def train(memories: List[Memory]): Agent = {
    this
  }

  def updateEpsilonRate(): Agent = {
    this
  }

  def removeEpsilon(): Agent = {
    this
  }
}
