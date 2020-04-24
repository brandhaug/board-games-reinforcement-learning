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
  val epochs: Int    = 1000 // number of batches we want the agent to run

  // MCTS
  val iterations: Int                    = 50
  val upperConfidenceBoundWeight: Double = 1.0

  // Neural Network
  val networkLearningRate: Double = 0.0001
  val networkHiddenLayerConfigs: Seq[HiddenLayerConfig] = Seq(
    HiddenLayerConfig(64, Activation.RELU),
    HiddenLayerConfig(64, Activation.RELU),
    HiddenLayerConfig(32, Activation.RELU)
  )

  val networkLoss      = LossFunctions.LossFunction.MSE
  val networkOptimizer = OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT
  val networkUpdaterString = "sgd"
  val networkMiniBatchSize: Int = 64 // TODO
  val epsilonRate: Double      = 1.0   // aka exploration rate
  val epsilonDecayRate: Double = 0.995
  val epsilonMinRate: Double   = 0.0
  val networkSaveInterval: Int = 100

  // Tournament
  val tournamentModelEpoch = 600

  def networkUpdater: IUpdater = networkUpdaterString match {
    case "adagrad" => new AdaGrad(networkLearningRate)
    case "sgd" => new Sgd(networkLearningRate)
    case "rms" => new RmsProp(networkLearningRate)
    case "adam" => new Adam(networkLearningRate)
  }

  def getModelPath(size: Int, epoch: Int = tournamentModelEpoch): String = {
    val learningRateString = networkLearningRate.toString.drop(2)
    f"models/${size}_${networkUpdaterString}_${learningRateString}_${epoch}"
  }
}

