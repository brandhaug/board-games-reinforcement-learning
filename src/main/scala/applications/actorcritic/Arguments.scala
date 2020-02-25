package applications.actorcritic

import environment.EnvironmentType
import environment.EnvironmentType.EnvironmentType

object Arguments {
  // 1. General
  val mapsDirectoryName: String = "boards"

  // 2. Environment
  val environmentType: EnvironmentType = EnvironmentType.PegSolitaire

  // 3. GUI
  val stepDelay: Double = 0.6

  // 4. Reinforcement Learning
  val episodes: Int = 1000 // number of games we want the applications.actorcritic.agent to play

  // 4.1 Actor
  val actorEpsilonRate: Double      = 1.0 // aka exploration rate
  val actorEpsilonDecayRate: Double = 0.995 // aka exploration decay rate
  val actorEpsilonMinRate: Double   = 0.05

  val actorDiscountFactor: Double       = 0.9 // aka gamma or discount rate [0.9, 0.99]
  val actorLearningRate: Double         = 0.01 // aka alpha or step size
  val actorEligibilityDecayRate: Double = 0.995

  //  4.2 Critic
  val criticDiscountFactor: Double       = 0.9
  val criticEligibilityDecayRate: Double = 0.995

  // 4.2.1 Table
  val tableCriticLearningRate: Double         = 0.01

  // 4.2.2 Neural Network
  val networkCriticLearningRate: Double         = 0.0005
  val criticNeuralNetworkDimensions: Seq[Int] = Seq(64, 64, 32)
}
