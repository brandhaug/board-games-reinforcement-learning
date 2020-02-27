package applications.actorcritic.agent

import agent.Agent
import environment.{Action, Environment, Memory}

import scala.util.Random

trait ActorCriticAgent extends Agent {
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

  def train(memory: List[Memory]): ActorCriticAgent
  def updateEpsilonRate(): ActorCriticAgent
  def removeEpsilon(): ActorCriticAgent
  def resetEligibilities(): ActorCriticAgent
}
