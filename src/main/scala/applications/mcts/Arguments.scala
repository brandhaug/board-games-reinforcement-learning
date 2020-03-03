package applications.mcts

import applications.mcts.PlayerType.PlayerType

object Arguments {
  // GUI
  val stepDelay: Double = 0.8

  // Game simulator
  val startingPlayerType: PlayerType = PlayerType.Mixed
  val verbose: Boolean = false

  // Training
  val epochs: Int    = 50 // number of batches we want the agent to run
  val batchSize: Int = 64  // should be able to handle 100

  // MCTS
  val simulations: Int                   = 500
  val upperConfidenceBoundWeight: Double = 1.0
}
