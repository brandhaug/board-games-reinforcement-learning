package applications.actorcritic.agent

object AgentType extends Enumeration {
  type AgentType = Value
  val TableLookup, NeuralNetwork = Value
}
