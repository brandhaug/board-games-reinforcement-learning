package baseagent

import environment.{Action, Environment}

import scala.util.Random

trait Agent {
  def act(environment: Environment): Action

  def randomAction(environment: Environment): Action = {
    val actionIndex = Random.nextInt(environment.possibleActions.size)
    environment.possibleActions(actionIndex)
  }
}
