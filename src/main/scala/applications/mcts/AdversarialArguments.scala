package applications.mcts

import applications.mcts.PlayerType.PlayerType

object AdversarialArguments {
  // GUI
  val stepDelay: Double = 0.8

  // Game simulator
  val startingPlayerType: PlayerType = PlayerType.Player1
  val verbose: Boolean               = false

  // Training
  val epochs: Int    = 5 // number of batches we want the agent to run
  val batchSize: Int = 10 // should be able to handle 100

  // MCTS
  val iterations: Int                    = 500
  val upperConfidenceBoundWeight: Double = 1.0
}
