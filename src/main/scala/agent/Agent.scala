package agent

import environment.{Action, Environment}

import scala.util.Random

abstract class Agent {
  val initialEnvironment: Environment
  def act(environment: Environment): Action
  def train(memory: Memory): Agent = {
    train(List(memory))
  }
  def train(memory: List[Memory]): Agent
  def randomAction(environment: Environment): Action = {
    val actionIndex = Random.nextInt(environment.possibleActions.length)
    environment.possibleActions(actionIndex)
  }
}
