package applications.mcts.agent

case object MonteCarloAgentType extends Enumeration {
  type MonteCarloAgentType = Value
  val TableLookup, NeuralNetwork = Value
}
