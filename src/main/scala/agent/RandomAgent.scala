package agent

import environment.{Action, Environment}
import environment.ActionType.ActionType

import scala.util.Random

case class RandomAgent(initialEnvironment: Environment, actionTypes: List[ActionType]) extends Agent {
  def act(environment: Environment): Action = {
    randomAction(environment)
  }

  def train(memories: List[Memory]): Agent = {
    this
  }
}
