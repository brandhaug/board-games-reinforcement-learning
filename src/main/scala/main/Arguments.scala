package main

import environment.enums.EnvironmentType
import environment.enums.EnvironmentType.EnvironmentType

object Arguments {
  // Environment
  val environmentType: EnvironmentType = EnvironmentType.PegSolitaire

  // GUI
  val stepDelay = 1

  // Peg Solitaire
  val emptyHoles: Set[Seq[Int]] = Set()

  // Reinforcement Learning
  val episodes: Int = 1000

  //  Critic
  val criticNeuralNetworkDimensions = Seq(15, 20, 30, 5, 1)
  val criticLearningRate = 0.01
  val criticEligibilityDecayRate = 0.0
  val criticDiscountFactor = 0.0

  // Actor
  val actorLearningRate = 0.01
  val actorEligibilityDecayRate = 0.0
  val actorDiscountFactor = 0.0
  // TODO: explorationRate ? 10: The initial value offor the actor’s-greedy strategy.  This value may change during a system run, inwhich case you may want to include a parameter for thedecay rate.
}
