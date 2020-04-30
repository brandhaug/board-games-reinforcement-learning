package applications.actorcritic.agent

import environment.Environment
import applications.actorcritic.SoloArguments._
import applications.mcts.PlayerType
import base.Memory


case class NetworkActorCriticAgent(initialEnvironment: Environment,
                                   stateActionValuePairMap: Map[String, List[ActionValuePair]] = Map(),
                                   epsilonRate: Double = actorEpsilonRate,
                                   actorEligibilities: Map[String, List[Double]] = Map(),
                                   criticEligibilities: Map[String, Double] = Map(),
                                   stateValueNetwork: StateValueNetwork)
    extends ActorCriticAgent {
  def train(memories: List[Memory]): ActorCriticAgent = {
    val memory = memories.last

    // 1. Current environment
    val stateKey    = memory.environment.toString
    val actionIndex = memory.environment.possibleActions.indexOf(memory.action)

    // 1.1. Critic
    val stateValue           = stateValueNetwork.predictValue(memory.environment.board.grid)

    val newCriticEligibilities = criticEligibilities + (stateKey -> 1.0)

    // 1.2 Actor
    val stateActionValuePairList   = stateActionValuePairMap.getOrElse(stateKey, memory.environment.possibleActions.map(action => ActionValuePair(action)))
    val newStateActionValuePairMap = stateActionValuePairMap + (stateKey -> stateActionValuePairList)

    val actorEligibilityList    = actorEligibilities.getOrElse(stateKey, memory.environment.possibleActions.map(_ => 0.0))
    val newActorEligibilityList = actorEligibilityList.updated(actionIndex, 1.0)
    val newActorEligibilities   = actorEligibilities + (stateKey -> newActorEligibilityList)

    // 2. Next environment

    // 2.1 Critic
    val nextStateValue          = stateValueNetwork.predictValue(memory.environment.board.grid)
    val temporalDifferenceError = memory.nextEnvironment.reward + (criticDiscountFactor * nextStateValue) - stateValue

    // 3. New agent
    val newAgent = NetworkActorCriticAgent(
      initialEnvironment,
      stateActionValuePairMap = newStateActionValuePairMap,
      epsilonRate = epsilonRate,
      actorEligibilities = newActorEligibilities,
      criticEligibilities = newCriticEligibilities,
      stateValueNetwork = stateValueNetwork
    )

    step(memories, newAgent, temporalDifferenceError)
  }

  def step(memories: List[Memory], currentAgent: NetworkActorCriticAgent, temporalDifferenceError: Double, memoryIndex: Int = 0): ActorCriticAgent = {
    if (memoryIndex > memories.length - 1) {
      currentAgent
    } else {
      val memory   = memories(memoryIndex)
      val stateKey = memory.environment.toString

      // Critic
      val stateValue           = stateValueNetwork.predictValue(memory.environment.board.grid)
      val criticEligibility = currentAgent.criticEligibilities(stateKey)

      val newStateValue        = stateValue + (tableCriticLearningRate * temporalDifferenceError * criticEligibility)
      currentAgent.stateValueNetwork.fit(memory.environment.board.grid, newStateValue, PlayerType.Player1)

      val newCriticEligibility   = criticDiscountFactor * criticEligibilityDecayRate * criticEligibility
      val newCriticEligibilities = currentAgent.criticEligibilities + (stateKey -> newCriticEligibility)

      // Actor
      val actionIndex = memory.environment.possibleActions.indexOf(memory.action)

      val stateActionValuePairList = currentAgent.stateActionValuePairMap(stateKey)
      val stateActionValuePair     = stateActionValuePairList(actionIndex)
      val value                = stateActionValuePair.value

      val actorEligibilityList = currentAgent.actorEligibilities(stateKey)
      val actorEligibility     = actorEligibilityList(actionIndex)

      val newValue                = value + (actorLearningRate * temporalDifferenceError * actorEligibility)
      val newStateActionValuePair     = ActionValuePair(memory.action, newValue)
      val newStateActionValuePairList = stateActionValuePairList.updated(actionIndex, newStateActionValuePair)
      val newStateActionValuePairMap  = currentAgent.stateActionValuePairMap + (stateKey -> newStateActionValuePairList)

      val newActorEligibility     = actorDiscountFactor * actorEligibilityDecayRate * actorEligibility
      val newActorEligibilityList = actorEligibilityList.updated(actionIndex, newActorEligibility)
      val newActorEligibilities   = currentAgent.actorEligibilities + (stateKey -> newActorEligibilityList)

      val newAgent = NetworkActorCriticAgent(
        initialEnvironment,
        stateActionValuePairMap = newStateActionValuePairMap,
        epsilonRate = epsilonRate,
        actorEligibilities = newActorEligibilities,
        criticEligibilities = newCriticEligibilities,
        stateValueNetwork = stateValueNetwork
      )

      step(memories, newAgent, temporalDifferenceError, memoryIndex = memoryIndex + 1)
    }
  }

  def updateEpsilonRate(): ActorCriticAgent = {
    val potentialNewEpsilonRate = epsilonRate * actorEpsilonDecayRate
    val newEpsilonRate          = if (potentialNewEpsilonRate >= actorEpsilonMinRate) potentialNewEpsilonRate else actorEpsilonMinRate
    NetworkActorCriticAgent(initialEnvironment, stateActionValuePairMap = stateActionValuePairMap, epsilonRate = newEpsilonRate, stateValueNetwork = stateValueNetwork)
  }

  def removeEpsilon(): ActorCriticAgent = {
    NetworkActorCriticAgent(initialEnvironment, stateActionValuePairMap = stateActionValuePairMap, epsilonRate = 0.0, stateValueNetwork = stateValueNetwork)
  }

  def resetEligibilities(): ActorCriticAgent = {
    NetworkActorCriticAgent(initialEnvironment, stateActionValuePairMap = stateActionValuePairMap, epsilonRate = epsilonRate, stateValueNetwork = stateValueNetwork)
  }

  override def toString: String = {
    s"StateActionValueMap: ${stateActionValuePairMap.size}, StateValueNetwork: ${stateValueNetwork.toString}, EpsilonRate: $epsilonRate"
  }
}
