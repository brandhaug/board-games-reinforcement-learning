package agent.enums

object AgentType extends Enumeration {
  type AgentType = Value
  val TableLookup, NeuralNetwork = Value
}
