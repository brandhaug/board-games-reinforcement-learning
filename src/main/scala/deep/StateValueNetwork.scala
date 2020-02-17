package deep

import environment.{Cell, Environment}
import main.Arguments
import org.deeplearning4j.nn.api.OptimizationAlgorithm
import org.deeplearning4j.nn.conf.inputs.InputType
import org.deeplearning4j.nn.conf.layers.{ConvolutionLayer, OutputLayer}
import org.deeplearning4j.nn.conf.{MultiLayerConfiguration, NeuralNetConfiguration}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.learning.config.Sgd
import org.nd4j.linalg.lossfunctions.LossFunctions

case class StateValueNetwork(initialEnvironment: Environment) {
  val channels      = 1
  val miniBatchSize = 1

  val model: MultiLayerNetwork = {
    val inputHeight = initialEnvironment.board.grid.size
    val inputWidth  = initialEnvironment.board.grid.head.size

    val builder = new NeuralNetConfiguration.Builder()
      .weightInit(WeightInit.XAVIER)
      .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
      .updater(new Sgd(Arguments.networkCriticLearningRate))
      .list()

    for (dimension <- Arguments.criticNeuralNetworkDimensions) {
      builder.layer(
        new ConvolutionLayer.Builder(3, 3)
          .stride(1, 1)
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

  def normalize(grid: List[List[Cell]]): Array[Array[Double]] = {
    val values    = grid.map(_.map(_.cellValue.toDouble))
    val flattened = values.flatten
    val max       = flattened.max
    val min       = flattened.min
    values.map(_.map(value => (value - min) / (max - min)).toArray).toArray
  }

  def preprocessInput(grid: List[List[Cell]]): INDArray = {
    val normalized = normalize(grid)
    val indArray   = Nd4j.create(normalized)
    indArray.reshape(Array(miniBatchSize, channels, grid.size, grid.head.size))
  }

  def preprocessLabel(label: Double): INDArray = {
    val indArray = Nd4j.create(Array(label))
    indArray.reshape(Array(miniBatchSize, 1))
  }

  def fit(grid: List[List[Cell]], labelValue: Double): Unit = {
    val input = preprocessInput(grid)
    val label = preprocessLabel(labelValue)
    model.fit(input, label)
  }

  def predict(grid: List[List[Cell]]): Double = {
    val input = preprocessInput(grid)
    model.predict(input).head
  }

  override def toString: String = model.numParams().toString
}
