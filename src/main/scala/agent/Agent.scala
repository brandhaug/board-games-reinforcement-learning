package agent

import environment.{Action, Environment, Memory}

import scala.util.Random

trait Agent {
  val initialEnvironment: Environment

  def act(environment: Environment): Action

  def randomAction(environment: Environment): Action = {
    val actionIndex = Random.nextInt(environment.possibleActions.size)
    environment.possibleActions(actionIndex)
  }

  def train(memory: List[Memory]): Agent
}
