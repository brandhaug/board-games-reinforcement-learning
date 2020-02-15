package main

import environment.EnvironmentType
import environment.EnvironmentType.EnvironmentType

object Arguments {
  // 1. General
  val mapsDirectoryName: String = "boards"

  // 2. Environment
  val environmentType: EnvironmentType = EnvironmentType.PegSolitaire

  // 3. GUI
  val stepDelay: Double = 2

  // 4. Reinforcement Learning
  val episodes: Int = 500 // number of games we want the agent to play

  // 4.1 Actor
  val actorEpsilonRate: Double      = 1.0 // aka exploration rate
  val actorEpsilonDecayRate: Double = 0.995 // 0.9995 // aka exploration decay rate
  val actorEpsilonMinRate: Double   = 0.05

  val actorDiscountFactor: Double       = 0.9 // aka gamma or discount rate [0.9, 0.99]
  val actorLearningRate: Double         = 0.1 // aka alpha or step size
  val actorEligibilityDecayRate: Double = 0.995

  //  4.2 Critic
  val criticDiscountFactor: Double       = 0.9
  val criticLearningRate: Double         = 0.1
  val criticEligibilityDecayRate: Double = 0.995

  // 4.2.2 Neural Network
  val criticNeuralNetworkDimensions: Seq[Int] = Seq(32) // cnn maxpool cnn maxpool dense
}
