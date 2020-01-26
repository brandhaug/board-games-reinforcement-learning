package agent

import environment.{Action, Environment}
import main.Arguments._

import scala.util.Random

case class TableAgent(initialEnvironment: Environment, table: Map[String, List[ActionReward]] = Map(), epsilonRate: Double = actorEpsilonRate) extends Agent {
  println(f"Table size: ${table.size}")
  println(f"Epsilon rate: $epsilonRate")

  def act(environment: Environment): Action = {
    val actionRewardList = table.getOrElse(environment.toString, List.empty)

    if (actionRewardList.isEmpty || Random.nextDouble() <= epsilonRate) {
      randomAction(environment)
    } else {
      actionRewardList.maxBy(_.reward).action
    }
  }

  def train(memories: List[Memory]): Agent = {

    /**
      * Updates Q(s, a) with Bellman Equation
      */
    val newKeyValuePairs = for {
      memory <- memories

      // Current reward
      tableKey         = memory.environment.toString
      oldActionRewards = table.getOrElse(tableKey, initializeActionRewardList(memory))
      oldActionReward <- oldActionRewards.filter(_.action.equals(memory.action))
      oldReward = oldActionReward.reward

      // Next environment
      nextTableKey        = memory.nextEnvironment.toString
      nextActionRewards   = table.getOrElse(nextTableKey, initializeActionRewardList(memory))
      maxNextActionReward = nextActionRewards.maxBy(_.reward)

      // Bellman equation
      newReward        = oldReward + (actorLearningRate * (memory.nextEnvironment.reward + (actorDiscountFactor * maxNextActionReward.reward) - oldReward))
      newActionReward  = ActionReward(oldActionReward.action, newReward)
      newActionRewards = oldActionRewards.filterNot(_.action.equals(memory.action)) :+ newActionReward
    } yield {
      tableKey -> newActionRewards
    }

    val newTable = table ++ newKeyValuePairs

    val newEpsilonRate = epsilonRate * actorEpsilonDecayRate
    TableAgent(initialEnvironment, newTable, epsilonRate = if (newEpsilonRate >= actorEpsilonMinRate) newEpsilonRate else actorEpsilonMinRate)
  }

  def initializeActionRewardList(memory: Memory): List[ActionReward] = {
    memory.environment.possibleActions.map(action => ActionReward(action, 0))
  }
}
