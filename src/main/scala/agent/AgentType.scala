package agent

object AgentType extends Enumeration {
  type AgentType = Value
  val TableLookup, NeuralNetwork, Random = Value
}
