package main

import environment.EnvironmentType
import environment.EnvironmentType.EnvironmentType

object Arguments {
  // 1. General
  val mapsDirectoryName = "boards"

  // 2. Environment
  val environmentType: EnvironmentType = EnvironmentType.PegSolitaire

  // 3. GUI
  val stepDelay = 0.2

  // 4. Reinforcement Learning
  val episodes: Int = 500 // number of games we want the agent to play

  // 4.1 Actor
  val actorEpsilonRate      = 1.0    // aka exploration rate
  val actorEpsilonDecayRate = 0.995  // aka exploration decay rate
  val actorEpsilonMinRate   = 0.0
  val actorDiscountFactor   = 0.95   // aka gamma or discount rate
  val actorLearningRate     = 0.001F // aka alpha or step size
  val actorEligibilityRate = 0.0
  val actorEligibilityDecayRate = 0.995

  //  4.2 Critic
  val criticEpsilonRate      = 1.0 // aka exploration rate
  val criticEpsilonDecayRate = 0.995 // aka exploration decay rate
  val criticEpsilonMinRate   = 0.01
  val criticDiscountFactor   = 0.0 // aka gamma
  val criticLearningRate     = 0.01

  // 4.2.2 Neural Network
  val criticNeuralNetworkDimensions = Seq(15, 20, 30, 5, 1)
}
