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
  final def iterate(environment: Environment, playerType: PlayerType, simulationsLeft: Int = AdversarialArguments.iterations): MonteCarloAgent = {
    if (simulationsLeft == 0) {
      this
    } else {
      val newAgent = traverse(environment, playerType)
      newAgent.iterate(environment, playerType, simulationsLeft - 1)
    }
  }

  @scala.annotation.tailrec
  private def traverse(environment: Environment, playerType: PlayerType, parents: List[Environment] = List.empty): MonteCarloAgent = {
    val stateKey    = environment.toString
    val stateVisits = stateVisitMap.getOrElse(stateKey, 0)

    if (parents.nonEmpty && stateVisits == 0) {
      // Rollout
      val reward = rollout(environment, playerType)
      backpropagate(reward, parents :+ environment)
    } else {
      // Select/Expand
      val rootKey         = if (parents.isEmpty) environment.toString else parents.head.toString
      val rootVisits      = stateVisitMap.getOrElse(rootKey, 0)
      val selectedAction  = selectAction(environment, playerType, rootVisits)
      val nextEnvironment = environment.step(selectedAction)

      if (nextEnvironment.isDone) {
        val reward = calculateReward(nextEnvironment, playerType)
        backpropagate(reward, parents :+ environment)
      } else {
        val nextPlayerType = PlayerType.getNextPlayerType(playerType)
        traverse(nextEnvironment, nextPlayerType, parents = parents :+ environment)
      }
    }
  }

  @scala.annotation.tailrec
  private def backpropagate(result: Double, environments: List[Environment]): MonteCarloAgent = {
    if (environments.isEmpty) {
      this
    } else {
      val environment = environments.last

      val stateKey    = environment.toString
      val stateVisits = stateVisitMap.getOrElse(stateKey, 0)
      val stateValue  = stateValueMap.getOrElse(stateKey, 0.0)

      val newStateValue    = stateValue + result
      val newStateVisitMap = stateVisitMap + (stateKey -> (stateVisits + 1))
      val newStateValueMap = stateValueMap + (stateKey -> newStateValue)

      val newAgent = MonteCarloAgent(newStateVisitMap, newStateValueMap)

      newAgent.backpropagate(result, environments.dropRight(1))
    }
  }

  @scala.annotation.tailrec
  private def rollout(environment: Environment, playerType: PlayerType): Double = {
    val selectedAction  = randomAction(environment)
    val nextEnvironment = environment.step(selectedAction)

    if (nextEnvironment.isDone) {
      calculateReward(nextEnvironment, playerType)
    } else {
      val nextPlayerType = PlayerType.getNextPlayerType(playerType)
      rollout(nextEnvironment, nextPlayerType)
    }
  }

  private def calculateReward(environment: Environment, playerType: PlayerType.PlayerType): Double = {
    if (playerType == PlayerType.Player1) environment.reward else -environment.reward
  }

  private def selectAction(environment: Environment, playerType: PlayerType, rootVisits: Int): Action = {
    playerType match {
      case PlayerType.Player1 =>
        environment.possibleActions.maxBy(action => {
          val nextEnvironment      = environment.step(action)

          if (nextEnvironment.isDone) {
            Double.MaxValue
          } else {
            val stateActionValue = getStateValue(nextEnvironment)
            val upperConfidenceBound = getUpperConfidenceBound(nextEnvironment, rootVisits)

            stateActionValue + upperConfidenceBound
          }
        })
      case PlayerType.Player2 =>
        environment.possibleActions.minBy(action => {
          val nextEnvironment      = environment.step(action)

          if (nextEnvironment.isDone) {
            Double.MinValue
          } else {
            val stateActionValue = getStateValue(nextEnvironment)
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
