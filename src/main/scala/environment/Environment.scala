package environment

import environment.ActionType.ActionType
import scalafx.scene.canvas.GraphicsContext

trait Environment {
  val board: Board
  val reward: Double
  val possibleActions: List[Action]
  val isDone: Boolean
  val actionTypes: Set[ActionType]
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
  def toggleCell(x: Int, y: Int): Environment
}
