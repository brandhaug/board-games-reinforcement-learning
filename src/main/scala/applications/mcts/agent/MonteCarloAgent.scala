package applications.mcts.agent

import base.Agent
import applications.mcts.{AdversarialArguments, PlayerType}
import applications.mcts.PlayerType.PlayerType
import environment.{Action, Environment}

case class MonteCarloAgent(stateVisitMap: Map[String, Int] = Map(), stateValueMap: Map[String, Double] = Map()) extends Agent {

  def act(environment: Environment, playerType: PlayerType): Action = {
    val rootVisits = stateVisitMap.getOrElse(environment.toString, 0)
    selectAction(environment, playerType, rootVisits)
  }

  @scala.annotation.tailrec
  final def iterate(environment: Environment, playerType: PlayerType, iterationsLeft: Int = AdversarialArguments.iterations): MonteCarloAgent = {
    if (iterationsLeft == 0) {
      this
    } else {
      val newAgent = traverse(environment, playerType)
      newAgent.iterate(environment, playerType, iterationsLeft - 1)
    }
  }

  @scala.annotation.tailrec
  private def traverse(environment: Environment, playerType: PlayerType, visitedStates: List[Environment] = List.empty): MonteCarloAgent = {
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

  @scala.annotation.tailrec
  private def backpropagate(result: Double, winningPlayerType: PlayerType, playerType: PlayerType, visitedStates: List[Environment]): MonteCarloAgent = {
    if (visitedStates.isEmpty) {
      this
    } else {
      val environment = visitedStates.last

      val stateKey    = environment.toString
      val stateVisits = stateVisitMap.getOrElse(stateKey, 0)
      val stateValue  = stateValueMap.getOrElse(stateKey, 0.0)

      val newStateValue    = if (playerType == winningPlayerType) stateValue + result else stateValue - result
      val newStateVisitMap = stateVisitMap + (stateKey -> (stateVisits + 1))
      val newStateValueMap = stateValueMap + (stateKey -> newStateValue)

      val newAgent       = MonteCarloAgent(newStateVisitMap, newStateValueMap)
      val nextPlayerType = PlayerType.getNextPlayerType(playerType)
      newAgent.backpropagate(result, winningPlayerType, nextPlayerType, visitedStates.dropRight(1))
    }
  }

  @scala.annotation.tailrec
  private def rollout(environment: Environment, playerType: PlayerType): RolloutResult = {
    val selectedAction  = randomAction(environment)
    val nextEnvironment = environment.step(selectedAction)
    val nextPlayerType  = PlayerType.getNextPlayerType(playerType)

    if (nextEnvironment.isDone) {
      RolloutResult(nextPlayerType, nextEnvironment.reward)
    } else {
      rollout(nextEnvironment, nextPlayerType)
    }
  }

  private def selectAction(environment: Environment, playerType: PlayerType, rootVisits: Int): Action = {
    playerType match {
      case PlayerType.Player1 =>
        environment.possibleActions.maxBy(action => {
          val nextEnvironment = environment.step(action)

          if (nextEnvironment.isDone) {
            Double.MaxValue
          } else {
            val stateActionValue     = getStateValue(nextEnvironment)
            val upperConfidenceBound = getUpperConfidenceBound(nextEnvironment, rootVisits)

            stateActionValue + upperConfidenceBound
          }
        })
      case PlayerType.Player2 =>
        environment.possibleActions.maxBy(action => {
          val nextEnvironment = environment.step(action)

          if (nextEnvironment.isDone) {
            Double.MaxValue
          } else {
            val stateActionValue     = getStateValue(nextEnvironment)
            val upperConfidenceBound = getUpperConfidenceBound(nextEnvironment, rootVisits)

            stateActionValue - upperConfidenceBound
          }
        })
    }
  }

  private def getStateValue(environment: Environment): Double = {
    val stateKey = environment.toString
    stateValueMap.getOrElse(stateKey, 0)
  }

  private def getUpperConfidenceBound(environment: Environment, rootVisits: Int): Double = {
    val stateKey    = environment.toString
    val stateVisits = stateVisitMap.getOrElse(stateKey, 0)

    if (stateVisits == 0) 1000
    else AdversarialArguments.upperConfidenceBoundWeight * Math.sqrt(Math.log(rootVisits) / (1 + stateVisits).toDouble)
  }
}

case class RolloutResult(playerType: PlayerType, reward: Double)
