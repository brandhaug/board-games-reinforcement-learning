package environment

import scalafx.scene.canvas.GraphicsContext

abstract class Environment {
  val grid: List[List[Int]]
  val reward: Double
  val possibleActions: List[Action]
  val isDone: Boolean
  def step(action: Action): Environment
  def render(gc: GraphicsContext): Unit
  def maxNextReward: Double = {
    val nextRewards = for {
      possibleAction <- possibleActions
      nextEnvironment = step(possibleAction)
    } yield {
      nextEnvironment.reward
    }

    if (nextRewards.nonEmpty) nextRewards.max else 0.0
  }
  override def toString: String = grid.flatten.map(_.toString).mkString("")
}
