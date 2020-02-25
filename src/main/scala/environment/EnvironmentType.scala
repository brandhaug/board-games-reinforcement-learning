package environment

object EnvironmentType extends Enumeration {
  type EnvironmentType = Value

  // Actor-critic
  val PegSolitaire: EnvironmentType.Value = Value(0)

  // MCTS
  val Nim: EnvironmentType.Value = Value(1)
  val Ledge: EnvironmentType.Value = Value(2)
}
