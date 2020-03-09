package applications.mcts.agent

import baseagent.Agent
import applications.mcts.{Arguments, PlayerType}
import applications.mcts.PlayerType.PlayerType
import environment.{Action, Environment}

case class MonteCarloAgent(stateVisitMap: Map[String, Int] = Map(),
                           stateValueMap: Map[String, Double] = Map()) extends Agent {

  def act(environment: Environment): Action = {
    val rootVisits = stateVisitMap.getOrElse(environment.toString, 0)
    selectAction(environment, PlayerType.Player1, rootVisits)
  }

  @scala.annotation.tailrec
  final def simulate(environment: Environment, simulationsLeft: Int = Arguments.simulations): MonteCarloAgent = {
    if (simulationsLeft == 0) {
      this
    } else {
      val newAgent = traverse(environment)
      newAgent.simulate(environment, simulationsLeft - 1)
    }
  }

  @scala.annotation.tailrec
  private def traverse(environment: Environment, playerType: PlayerType = PlayerType.Player1, parents: List[Environment] = List.empty): MonteCarloAgent = {
    val stateKey    = environment.toString
    val stateVisits = stateVisitMap.getOrElse(stateKey, 0)

    if (environment.possibleActions.isEmpty) {
      this
    } else if (parents.nonEmpty && stateVisits == 0) {
      val rolloutResult = rollout(environment)
      backpropagate(rolloutResult, parents :+ environment)
    } else {
      val rootKey         = if (parents.isEmpty) environment.toString else parents.head.toString
      val rootVisits      = stateVisitMap.getOrElse(rootKey, 0)
      val selectedAction  = selectAction(environment, playerType, rootVisits)
      val nextEnvironment = environment.step(selectedAction)
      val nextPlayerType  = PlayerType.getNextPlayerType(playerType)
      traverse(nextEnvironment, nextPlayerType, parents = parents :+ environment)
    }
  }

  @scala.annotation.tailrec
  private def backpropagate(result: Double, environments: List[Environment]): MonteCarloAgent = {
    if (environments.isEmpty) {
      this
    } else {
      val environment = environments.head

      val stateKey    = environment.toString
      val stateVisits = stateVisitMap.getOrElse(stateKey, 0)
      val stateValue  = stateValueMap.getOrElse(stateKey, 0.0)

      val newStateValue    = stateValue + result
      val newStateVisitMap = stateVisitMap + (stateKey -> (stateVisits + 1))
      val newStateValueMap = stateValueMap + (stateKey -> newStateValue)

      val newAgent = MonteCarloAgent(newStateVisitMap, newStateValueMap)

      newAgent.backpropagate(result, environments.drop(1))
    }
  }

  @scala.annotation.tailrec
  private def rollout(environment: Environment, playerType: PlayerType = PlayerType.Player1): Double = {
    val selectedAction  = randomAction(environment)
    val nextEnvironment = environment.step(selectedAction)

    if (nextEnvironment.isDone) {
      if (playerType == PlayerType.Player1) nextEnvironment.reward
      else 0.0
    } else {
      val nextPlayerType = PlayerType.getNextPlayerType(playerType)
      rollout(nextEnvironment, nextPlayerType)
    }
  }

  private def selectAction(environment: Environment, playerType: PlayerType, rootVisits: Int): Action = {
    playerType match {
      case PlayerType.Player1 =>
        environment.possibleActions.maxBy(action => {
          val nextEnvironment      = environment.step(action)
          val stateActionValue     = getStateValue(nextEnvironment)
          val upperConfidenceBound = getUpperConfidenceBound(nextEnvironment, rootVisits)

          stateActionValue + upperConfidenceBound
        })
      case PlayerType.Player2 =>
        environment.possibleActions.minBy(action => {
          val nextEnvironment      = environment.step(action)
          val stateActionValue     = getStateValue(nextEnvironment)
          val upperConfidenceBound = getUpperConfidenceBound(nextEnvironment, rootVisits)

          stateActionValue - upperConfidenceBound
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

    if (stateVisits == 0) Double.MaxValue
    else Arguments.upperConfidenceBoundWeight * Math.sqrt(Math.log(rootVisits) / (1 + stateVisits).toDouble)
  }
}
