package agent

import environment.{Action, Environment}
import environment.ActionType.ActionType

case class NetworkAgent(initialEnvironment: Environment, actionTypes: List[ActionType]) extends Agent {
  def act(environment: Environment): Action = {
    //    val target = Arguments.actorEligibilityDecayRate * np.amax(model.predict(next_state))
    ???
  }

  def train(memories: List[Memory]): Agent = {
    ???
  }
}
