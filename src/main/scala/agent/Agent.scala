package agent

import environment.{Action, Environment}

import scala.util.Random

trait Agent {


  val initialEnvironment: Environment
  val epsilonRate: Double
  def act(environment: Environment): Action
  def train(memory: Memory): Agent = {
    train(List(memory))
  }
  def train(memory: List[Memory]): Agent
  def randomAction(environment: Environment): Action = {
    val actionIndex = Random.nextInt(environment.possibleActions.length)
    environment.possibleActions(actionIndex)
  }
  def updateEpsilonRate(): Agent
  def removeEpsilon(): Agent
  def resetEligibilities(): Agent
}
