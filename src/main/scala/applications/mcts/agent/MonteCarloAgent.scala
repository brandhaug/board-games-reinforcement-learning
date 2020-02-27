package applications.mcts.agent

import agent.Agent
import environment.{Action, Environment, Memory}

case class MonteCarloAgent(initialEnvironment: Environment) extends Agent {
  def act(environment: Environment): Action = {
    ???
  }

  def trainBatch(memory: List[List[Memory]]): MonteCarloAgent = {
    ???
  }

  def train(memory: List[Memory]): Agent = {
    ???
  }

}
