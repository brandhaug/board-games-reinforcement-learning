package agent

import environment.{Action, Environment}
import main.Arguments._

import scala.util.Random

case class TableAgent(initialEnvironment: Environment,
                      stateActionRewardMap: Map[String, List[ActionReward]] = Map(),
                      stateValueMap: Map[String, Double] = Map(),
                      epsilonRate: Double = actorEpsilonRate,
                      actorEligibilities: Map[String, List[Double]] = Map(),
                      criticEligibilities: Map[String, Double] = Map())
    extends Agent {

  def act(environment: Environment): Action = {
    val stateActionRewardList = stateActionRewardMap.getOrElse(environment.toString, List.empty)

    if (stateActionRewardList.isEmpty || Random.nextDouble() <= epsilonRate) {
      randomAction(environment)
    } else {
      stateActionRewardList.maxBy(_.reward).action
    }
  }

  def train(memories: List[Memory]): Agent = {
    val memory = memories.last

    // 1. Current environment
    val stateKey    = memory.environment.toString
    val actionIndex = memory.environment.possibleActions.indexOf(memory.action)

    // 1.1. Critic
    val stateValue       = stateValueMap.getOrElse(stateKey, Random.nextDouble())
    val newStateValueMap = stateValueMap + (stateKey -> stateValue)

    val newCriticEligibilities = criticEligibilities + (stateKey -> 1.0)

    // 1.2 Actor
    val stateActionRewardList   = stateActionRewardMap.getOrElse(stateKey, memory.environment.possibleActions.map(action => ActionReward(action)))
    val newStateActionRewardMap = stateActionRewardMap + (stateKey -> stateActionRewardList)

    val actorEligibilityList    = actorEligibilities.getOrElse(stateKey, memory.environment.possibleActions.map(_ => 0.0))
    val newActorEligibilityList = actorEligibilityList.updated(actionIndex, 1.0)
    val newActorEligibilities   = actorEligibilities + (stateKey -> newActorEligibilityList)

    // 2. Next environment
    val nextStateKey = memory.nextEnvironment.toString

    // 2.1 Critic
    val nextStateValue          = stateValueMap.getOrElse(nextStateKey, Random.nextDouble())
    val temporalDifferenceError = memory.nextEnvironment.reward + (criticDiscountFactor * nextStateValue) - stateValue

    // 3. New agent
    val newAgent = TableAgent(initialEnvironment, newStateActionRewardMap, newStateValueMap, epsilonRate, newActorEligibilities, newCriticEligibilities)
    step(memories, newAgent, temporalDifferenceError)
  }

  def step(memories: List[Memory], currentAgent: TableAgent, temporalDifferenceError: Double, memoryIndex: Int = 0): Agent = {
    if (memoryIndex > memories.length - 1) {
      TableAgent(initialEnvironment, currentAgent.stateActionRewardMap, currentAgent.stateValueMap, epsilonRate)
    } else {
      val memory   = memories(memoryIndex)
      val stateKey = memory.environment.toString

      // Critic
      val stateValue        = currentAgent.stateValueMap(stateKey)
      val criticEligibility = currentAgent.criticEligibilities(stateKey)

      val newStateValue    = stateValue + (criticLearningRate * temporalDifferenceError * criticEligibility)
      val newStateValueMap = stateValueMap + (stateKey -> newStateValue)

      val newCriticEligibility   = criticDiscountFactor * criticEligibilityDecayRate * criticEligibility
      val newCriticEligibilities = currentAgent.criticEligibilities + (stateKey -> newCriticEligibility)

      // Actor
      val actionIndex = memory.environment.possibleActions.indexOf(memory.action)

      val stateActionRewardList = currentAgent.stateActionRewardMap(stateKey)
      val stateActionReward     = stateActionRewardList(actionIndex)
      val reward                = stateActionReward.reward

      val actorEligibilityList = currentAgent.actorEligibilities(stateKey)
      val actorEligibility     = actorEligibilityList(actionIndex)

      val newReward                = reward + (actorLearningRate * temporalDifferenceError * actorEligibility)
      val newStateActionReward     = ActionReward(memory.action, newReward)
      val newStateActionRewardList = stateActionRewardList.updated(actionIndex, newStateActionReward)
      val newStateActionRewardMap  = currentAgent.stateActionRewardMap + (stateKey -> newStateActionRewardList)

      val newActorEligibility     = actorDiscountFactor * actorEligibilityDecayRate * actorEligibility
      val newActorEligibilityList = actorEligibilityList.updated(actionIndex, newActorEligibility)
      val newActorEligibilities   = currentAgent.actorEligibilities + (stateKey -> newActorEligibilityList)

      val newAgent = TableAgent(initialEnvironment, newStateActionRewardMap, newStateValueMap, epsilonRate, newActorEligibilities, newCriticEligibilities)
      step(memories, newAgent, temporalDifferenceError, memoryIndex = memoryIndex + 1)
    }
  }

  def updateEpsilonRate(): Agent = {
    val newEpsilonRate = epsilonRate * actorEpsilonDecayRate
    TableAgent(initialEnvironment, stateActionRewardMap, stateValueMap, epsilonRate = if (newEpsilonRate >= actorEpsilonMinRate) newEpsilonRate else actorEpsilonMinRate)
  }

  def removeEpsilon(): Agent = {
    TableAgent(initialEnvironment, stateActionRewardMap, stateValueMap, epsilonRate = 0)
  }

  override def toString: String = {
    s"StateActionRewardMap: ${stateActionRewardMap.size}, StateValueMap: ${stateValueMap.size}, EpsilonRate: $epsilonRate"
  }
}
