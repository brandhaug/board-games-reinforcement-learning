package applications.actorcritic.agent

import applications.actorcritic.SoloArguments
import base.Network
import environment.{Cell, Environment}
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.inputs.InputType
import org.deeplearning4j.nn.conf.layers.{DenseLayer, OutputLayer}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.learning.config.Sgd
import org.nd4j.linalg.lossfunctions.LossFunctions

case class StateValueNetwork(initialEnvironment: Environment) extends Network {
  val channels      = 1
  val miniBatchSize = 1

  val model: MultiLayerNetwork = {
    val inputHeight = initialEnvironment.board.grid.size
    val inputWidth  = initialEnvironment.board.grid.head.size

    val builder = new NeuralNetConfiguration.Builder()
      .weightInit(WeightInit.XAVIER)
      .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
      .updater(new Sgd(SoloArguments.networkCriticLearningRate))
      .list()

    for (dimension <- SoloArguments.criticNeuralNetworkDimensions) {
      builder.layer(
        new DenseLayer.Builder()
          .nOut(dimension)
          .activation(Activation.RELU)
          .build())
    }

    val conf = builder
      .layer(
        new OutputLayer.Builder(LossFunctions.LossFunction.RECONSTRUCTION_CROSSENTROPY)
          .nOut(1)
          .activation(Activation.RELU)
          .build())
      .setInputType(InputType.convolutional(inputHeight, inputWidth, channels))
      .build()

    val net = new MultiLayerNetwork(conf);
    net.init()

    net
  }

  def predictValue(grid: List[List[Cell]]): Double = {
    predict(grid).head
  }
}
