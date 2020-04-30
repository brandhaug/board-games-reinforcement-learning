package applications.mcts.agent

import applications.mcts.PlayerType
import applications.mcts.PlayerType.PlayerType
import environment.Environment
import environment.adversarial.ActionVisitMemory

case class MonteCarloTableAgent(stateVisitMap: Map[String, Int] = Map(), stateValueMap: Map[String, Double] = Map()) extends MonteCarloAgent {

  @scala.annotation.tailrec
  final def backpropagate(result: Double, winningPlayerType: PlayerType, playerType: PlayerType, visitedStates: List[Environment]): MonteCarloAgent = {
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

      val newAgent       = MonteCarloTableAgent(newStateVisitMap, newStateValueMap)
      val nextPlayerType = PlayerType.getNextPlayerType(playerType)
      newAgent.backpropagate(result, winningPlayerType, nextPlayerType, visitedStates.dropRight(1))
    }
  }
  @scala.annotation.tailrec
  final def rollout(environment: Environment, playerType: PlayerType): RolloutResult = {
    val selectedAction  = randomAction(environment)
    val nextEnvironment = environment.step(selectedAction)
    val nextPlayerType  = PlayerType.getNextPlayerType(playerType)

    if (nextEnvironment.isDone) {
      RolloutResult(nextPlayerType, nextEnvironment.reward)
    } else {
      rollout(nextEnvironment, nextPlayerType)
    }
  }

  def train(actionVisitMemoriesList: List[List[ActionVisitMemory]]): MonteCarloAgent = {
    this
  }

  def save(size: Int, epoch: Int): Unit = {
  }

  def reset: MonteCarloAgent = {
    MonteCarloTableAgent()
  }
}
