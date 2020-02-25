package applications.actorcritic.agent

import environment.{Action, Environment}

import scala.util.Random

trait Agent {
  val initialEnvironment: Environment
  val stateActionRewardMap: Map[String, List[ActionReward]]
  val epsilonRate: Double
  val actorEligibilities: Map[String, List[Double]]
  val criticEligibilities: Map[String, Double]

  def act(environment: Environment): Action = {
    val stateActionRewardList = stateActionRewardMap.getOrElse(environment.toString, List.empty)

    if (stateActionRewardList.isEmpty || Random.nextDouble() <= epsilonRate) {
      randomAction(environment)
    } else {
      stateActionRewardList.maxBy(_.reward).action
    }
  }

  private def randomAction(environment: Environment): Action = {
    val actionIndex = Random.nextInt(environment.possibleActions.length)
    environment.possibleActions(actionIndex)
  }

  def train(memory: List[Memory]): Agent
  def updateEpsilonRate(): Agent
  def removeEpsilon(): Agent
  def resetEligibilities(): Agent
}
