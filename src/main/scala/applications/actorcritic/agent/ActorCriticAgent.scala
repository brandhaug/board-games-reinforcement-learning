package applications.actorcritic.agent

import baseagent.Agent
import environment.{Action, Environment, Memory}

import scala.util.Random

trait ActorCriticAgent extends Agent {
  val initialEnvironment: Environment
  val stateActionValuePairMap: Map[String, List[ActionValuePair]]
  val epsilonRate: Double
  val actorEligibilities: Map[String, List[Double]]
  val criticEligibilities: Map[String, Double]

  def act(environment: Environment): Action = {
    val stateActionRewardList = stateActionValuePairMap.getOrElse(environment.toString, List.empty)

    if (stateActionRewardList.isEmpty || Random.nextDouble() <= epsilonRate) {
      randomAction(environment)
    } else {
      stateActionRewardList.maxBy(_.value).action
    }
  }

  def train(memory: List[Memory]): ActorCriticAgent
  def updateEpsilonRate(): ActorCriticAgent
  def removeEpsilon(): ActorCriticAgent
  def resetEligibilities(): ActorCriticAgent
}
