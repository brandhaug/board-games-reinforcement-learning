package applications.mcts

object Arguments {
  // GUI
  val stepDelay: Double = 2

  // Game simulator
  val startingPlayerType = PlayerType.Mixed
  val epochs: Int = 100 // number of batches we want the agent to play
  val batchSize = 64 // 100
  val rollouts = 15
}
