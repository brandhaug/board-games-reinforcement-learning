package applications.mcts

import applications.mcts.PlayerType.PlayerType
import applications.mcts.agent.HiddenLayerConfig
import org.deeplearning4j.nn.api.{OptimizationAlgorithm, Updater}
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.{AdaGrad, Adam, IUpdater, RmsProp, Sgd}
import org.nd4j.linalg.learning.{AdaGradUpdater, GradientUpdater, RmsPropUpdater, SgdUpdater}
import org.nd4j.linalg.lossfunctions.LossFunctions

object AdversarialArguments {
  // GUI
  val stepDelay: Double = 0.8

  // Game simulator
  val startingPlayerType: PlayerType = PlayerType.Player1
  val verbose: Boolean               = false

  // Training
  val epochs: Int    = 10 // number of batches we want the agent to run
  val batchSize: Int = 1  // should be able to handle 100

  // MCTS
  val iterations: Int                    = 50
  val upperConfidenceBoundWeight: Double = 1.0

  // Neural Network
  val networkLearningRate: Double = 0.0005
  val networkHiddenLayerConfigs: Seq[HiddenLayerConfig] = Seq(
    HiddenLayerConfig(64, Activation.RELU),
    HiddenLayerConfig(64, Activation.RELU),
    HiddenLayerConfig(32, Activation.RELU)
  )

  val networkLoss      = LossFunctions.LossFunction.MSE
  val networkOptimizer = OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT

//  val networkUpdater: IUpdater = new AdaGrad(networkLearningRate)
  val networkUpdater: IUpdater = new Sgd(networkLearningRate)
//  val networkUpdater: IUpdater = new RmsProp(networkLearningRate)
//  val networkUpdater: IUpdater = new Adam(networkLearningRate)
  val networkMiniBatchSize: Int = 64

  val epsilonRate: Double      = 1.0   // aka exploration rate
  val epsilonDecayRate: Double = 0.995
  val epsilonMinRate: Double   = 0.0

  // TOPP
  val networkSaves: Int = 5
//  The number (M) of ANETs to be cached in preparation for a TOPP. These should be cached, starting with anuntrained net prior to episode 1, at a fixed interval throughout the training episodes.
//  The number of games, G, to be played between any two ANET-based agents that meet during the round-robinplay of the TOPP
}
