package applications.mcts.agent

import applications.mcts.{AdversarialArguments, PlayerType}
import applications.mcts.PlayerType.PlayerType
import environment.Environment
import environment.adversarial.ActionVisitMemory
import utils.ListUtils

import scala.util.Random

case class MonteCarloNetworkAgent(stateVisitMap: Map[String, Int] = Map(), stateValueMap: Map[String, Double] = Map(), stateActionNetwork: StateActionNetwork, epsilonRate: Double = AdversarialArguments.epsilonRate) extends MonteCarloAgent {

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

      val newAgent       = MonteCarloNetworkAgent(newStateVisitMap, newStateValueMap, stateActionNetwork, epsilonRate) // This is tjjhe only diff
      val nextPlayerType = PlayerType.getNextPlayerType(playerType)
      newAgent.backpropagate(result, winningPlayerType, nextPlayerType, visitedStates.dropRight(1))
    }
  }

  @scala.annotation.tailrec
  final def rollout(environment: Environment, playerType: PlayerType): RolloutResult = {
    val selectedAction = if (Random.nextDouble() <= epsilonRate) {
      randomAction(environment)
    } else {
      val selectedActionCell = stateActionNetwork.predictActionCell(environment.board.grid)
      environment.possibleActions.filter(action => action.xIndex == selectedActionCell.xIndex && action.yIndex == selectedActionCell.yIndex).head
    }

    val nextEnvironment    = environment.step(selectedAction)
    val nextPlayerType     = PlayerType.getNextPlayerType(playerType)

    if (nextEnvironment.isDone) {
      RolloutResult(nextPlayerType, nextEnvironment.reward)
    } else {
      rollout(nextEnvironment, nextPlayerType)
    }
  }

  def train(actionVisitMemoriesList: List[List[ActionVisitMemory]]): MonteCarloAgent = {
    actionVisitMemoriesList.foreach(actionVisitMemories => {
      val environment = actionVisitMemories.head.environment
      val visitDistribution = ListUtils.softMax(actionVisitMemories.map(_.visits.toDouble))
      val labels = environment.board.grid.flatten.map(cell => {
        val matchingActionVisitMemory = actionVisitMemories.find(actionVisitMemory => actionVisitMemory.action.xIndex == cell.xIndex && actionVisitMemory.action.yIndex == cell.yIndex)

        if (matchingActionVisitMemory.nonEmpty) {
          visitDistribution(actionVisitMemories.indexOf(matchingActionVisitMemory.get))
        } else {
          0.0
        }
      })

      stateActionNetwork.fit(environment.board.grid, labels)
    })

    val potentialNewEpsilonRate = epsilonRate * AdversarialArguments.epsilonDecayRate
    val newEpsilonRate          = if (potentialNewEpsilonRate >= AdversarialArguments.epsilonMinRate) potentialNewEpsilonRate else AdversarialArguments.epsilonMinRate

    println(f"Epsilon rate: $newEpsilonRate")
    val newAgent       = MonteCarloNetworkAgent(stateActionNetwork = stateActionNetwork, epsilonRate = newEpsilonRate)
    newAgent
  }

}
