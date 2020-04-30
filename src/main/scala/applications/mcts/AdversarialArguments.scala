package applications.mcts

import applications.mcts.PlayerType.PlayerType
import applications.mcts.agent.{HiddenLayerConfig, HiddenLayerType}
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.{AdaGrad, Adam, IUpdater, RmsProp, Sgd}
import org.nd4j.linalg.lossfunctions.LossFunctions

object AdversarialArguments {
  // GUI
  val stepDelay: Double = 0.8

  // Game simulator
  val startingPlayerType: PlayerType = PlayerType.Mixed
  val verbose: Boolean               = false

  // Training
  val epochs: Int    = 500 // number of batches we want the agent to run

  // MCTS
  val iterations: Int                    = 50
  val upperConfidenceBoundWeight: Double = 1.0

  // Neural Network
  val networkLearningRate: Double = 0.0001
  val networkHiddenLayerConfigs: Seq[HiddenLayerConfig] = Seq(
    HiddenLayerConfig(HiddenLayerType.Dense, 100, Activation.TANH),
    HiddenLayerConfig(HiddenLayerType.Dense, 200, Activation.TANH),
  )
  val networkLoss      = LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY
  val networkOptimizer = OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT
  val networkUpdaterString = "rms"
  val networkBatchSize: Int = 10
  val epsilonRate: Double      = 1.0   // aka exploration rate
  val epsilonDecayRate: Double = 0.995
  val epsilonMinRate: Double   = 0.0
  val networkSaveInterval: Int = 5

  // Tournament
  val tournamentModelEpoch = 5
  val tournamentGames = 5

  // Functions
  def networkUpdater: IUpdater = networkUpdaterString match {
    case "adagrad" => new AdaGrad(networkLearningRate)
    case "sgd" => new Sgd(networkLearningRate)
    case "rms" => new RmsProp(networkLearningRate)
    case "adam" => new Adam(networkLearningRate)
  }

  def getModelPath(size: Int, epoch: Int = tournamentModelEpoch): String = {
    val learningRateString = networkLearningRate.toString.replace(".", "")
    val epsilonDecayRateString = epsilonDecayRate.toString.drop(2)
    val hiddenLayersString = networkHiddenLayerConfigs.map(_.dimension).mkString("-")
    f"models/${size}_${hiddenLayersString}_${networkUpdaterString}_${learningRateString}_${iterations}_${epsilonDecayRateString}_${epoch}"
  }
}

