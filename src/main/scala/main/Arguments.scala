package main

import environment.EnvironmentType
import environment.EnvironmentType.EnvironmentType

object Arguments {
  // General
  val mapsDirectoryName = "boards"

  // Environment
  val environmentType: EnvironmentType = EnvironmentType.PegSolitaire

  // GUI
  val stepDelay = 0.2

  // Peg Solitaire
  val emptyHoles: Set[(Int, Int)] = Set()

  // Reinforcement Learning
  val episodes: Int = 20000 // number of games we want the agent to play

  // Actor
  val actorEligibilityDecayRate = 0.0
  val actorDiscountFactor       = 0.95 // aka gamma or discount rate
  val actorEpsilonRate          = 1.0 // aka exploration rate
  val actorEpsilonMinRate       = 0.01 // aka exploration rate
  val actorEpsilonDecayRate     = 0.995 // aka exploration decay rate
  val actorLearningRate         = 0.001F // aka alpha

  //  Critic
  val criticNeuralNetworkDimensions = Seq(15, 20, 30, 5, 1)
  val criticLearningRate            = 0.01
  val criticEligibilityDecayRate    = 0.0
  val criticDiscountFactor          = 0.0 // aka gamma

  // Neural Network
  val networkShape = Seq()
}
