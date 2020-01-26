package agent

import environment.ActionType.ActionType
import environment.{Action, Environment}

import scala.util.Random

abstract class Agent {
  val initialEnvironment: Environment
  val actionTypes: List[ActionType]
  def act(environment: Environment): Action
  def train(memory: List[Memory]): Agent
  def randomAction(environment: Environment): Action = {
    val actionIndex = Random.nextInt(environment.possibleActions.length)
    environment.possibleActions(actionIndex)
  }
}
