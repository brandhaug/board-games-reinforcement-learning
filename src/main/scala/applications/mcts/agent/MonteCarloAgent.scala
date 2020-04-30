package applications.mcts.agent

import applications.mcts.{AdversarialArguments, PlayerType}
import applications.mcts.PlayerType.PlayerType
import base.Agent
import environment.adversarial.ActionVisitMemory
import environment.{Action, Environment}

trait MonteCarloAgent extends Agent {
  val stateVisitMap: Map[String, Int]
  val stateValueMap: Map[String, Double]

  @scala.annotation.tailrec
  final def iterate(environment: Environment, playerType: PlayerType, iterationsLeft: Int = AdversarialArguments.iterations): MonteCarloAgent = {
    if (iterationsLeft == 0) {
      this
    } else {
      val newAgent = traverse(environment, playerType)
      newAgent.iterate(environment, playerType, iterationsLeft - 1)
    }
  }

  def act(environment: Environment, playerType: PlayerType): Action = {
    val rootVisits = stateVisitMap.getOrElse(environment.toString, 0)
    selectAction(environment, playerType, rootVisits)
  }

  @scala.annotation.tailrec
  final def traverse(environment: Environment, playerType: PlayerType, visitedStates: List[Environment] = List.empty): MonteCarloAgent = {
    val stateKey    = environment.toString
    val stateVisits = stateVisitMap.getOrElse(stateKey, 0)

    if (visitedStates.nonEmpty && stateVisits == 0) {
      // Rollout
      val rolloutResult = rollout(environment, playerType)
      backpropagate(rolloutResult.reward, rolloutResult.playerType, rolloutResult.playerType, visitedStates :+ environment)
    } else {
      // Select/Expand
      val rootKey         = if (visitedStates.isEmpty) environment.toString else visitedStates.head.toString
      val rootVisits      = stateVisitMap.getOrElse(rootKey, 0)
      val selectedAction  = selectAction(environment, playerType, rootVisits)
      val nextEnvironment = environment.step(selectedAction)
      val nextPlayerType  = PlayerType.getNextPlayerType(playerType)

      if (nextEnvironment.isDone) {
        val reward = nextEnvironment.reward
        backpropagate(reward, nextPlayerType, playerType, visitedStates :+ environment)
      } else {

        traverse(nextEnvironment, nextPlayerType, visitedStates = visitedStates :+ environment)
      }
    }
  }

  private def selectAction(environment: Environment, playerType: PlayerType, rootVisits: Int): Action = {
    environment.possibleActions.maxBy(action => {
      val nextEnvironment = environment.step(action)

      if (nextEnvironment.isDone) {
        Double.MaxValue
      } else {
        val stateActionValue = getStateValue(nextEnvironment)
        val upperConfidenceBound = getUpperConfidenceBound(nextEnvironment, rootVisits)

        playerType match {
          case PlayerType.Player1 => stateActionValue + upperConfidenceBound
          case PlayerType.Player2 => stateActionValue - upperConfidenceBound
        }
      }
    })
  }

  private def getStateValue(environment: Environment): Double = {
    val stateKey = environment.toString
    stateValueMap.getOrElse(stateKey, 0.0)
  }

  private def getUpperConfidenceBound(environment: Environment, rootVisits: Int): Double = {
    val stateKey    = environment.toString
    val stateVisits = stateVisitMap.getOrElse(stateKey, 0)

    if (stateVisits == 0) 1000
    else AdversarialArguments.upperConfidenceBoundWeight * Math.sqrt(Math.log(rootVisits) / (1 + stateVisits).toDouble)
  }

  def getActionVisitMemories(environment: Environment, playerType: PlayerType): List[ActionVisitMemory] = {
    environment.possibleActions.map(action => {
      val nextEnvironment = environment.step(action)
      val nextEnvironmentKey = nextEnvironment.toString
      val nextEnvironmentVisits = stateVisitMap.getOrElse(nextEnvironmentKey, 0)

      ActionVisitMemory(environment, action, nextEnvironment, nextEnvironmentVisits, playerType)
    })
  }

  def backpropagate(result: Double, winningPlayerType: PlayerType, playerType: PlayerType, visitedStates: List[Environment]): MonteCarloAgent
  def rollout(environment: Environment, playerType: PlayerType): RolloutResult
  def train(actionVisitMemoriesList: List[List[ActionVisitMemory]]): MonteCarloAgent
  def save(size: Int, epoch: Int): Unit
  def reset: MonteCarloAgent
}

case class RolloutResult(playerType: PlayerType, reward: Double)
