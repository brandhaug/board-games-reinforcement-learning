package applications.mcts.agent

import java.io.File

import applications.mcts.AdversarialArguments
import base.Network
import environment.{Cell, Environment, EnvironmentType}
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.inputs.InputType
import org.deeplearning4j.nn.conf.layers.{DenseLayer, OutputLayer}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import utils.ListUtils

case class StateActionNetwork(initialEnvironment: Environment, pathName: Option[String] = None) extends Network {
  val channels: Int      = 1
  val miniBatchSize: Int = AdversarialArguments.networkMiniBatchSize

  val model: MultiLayerNetwork = {
    val inputHeight     = initialEnvironment.board.grid.size
    val inputWidth      = initialEnvironment.board.grid.head.size
    val outputDimension = initialEnvironment.board.grid.flatten.size

    val builder = new NeuralNetConfiguration.Builder()
      .weightInit(WeightInit.XAVIER)
      .optimizationAlgo(AdversarialArguments.networkOptimizer)
      .updater(AdversarialArguments.networkUpdater)
      .list()

    for (hiddenLayerConfig <- AdversarialArguments.networkHiddenLayerConfigs) {
      builder.layer(
        new DenseLayer.Builder()
          .nOut(hiddenLayerConfig.dimension)
          .activation(hiddenLayerConfig.activation)
          .build())
    }

    val conf = builder
      .layer(
        new OutputLayer.Builder(AdversarialArguments.networkLoss)
          .nOut(outputDimension)
          .activation(Activation.SOFTMAX)
          .build())
      .setInputType(InputType.convolutional(inputHeight, inputWidth, channels))
      .build()

    val net = pathName match {
      case Some(pathName) =>
        val file = new File(pathName)
        MultiLayerNetwork.load(file, false)
      case None =>
        new MultiLayerNetwork(conf);
    }

    net.init()

    net
  }

  override def predict(grid: List[List[Cell]]): List[Double] = {
    val input      = preprocessInput(grid)
    val prediction = model.output(input).toDoubleVector.toList

    val updatedPredictions = for {
      (cell, i) <- grid.flatten.zipWithIndex
      cellPrediction = prediction(i)
    } yield {
      if (cell.isEmpty) cellPrediction
      else 0.0
    }

    ListUtils.softMax(updatedPredictions)
  }

  def predictActionCell(grid: List[List[Cell]]): Cell = {
    val predictions   = predict(grid)
    val maxIndex      = predictions.indexOf(predictions.max)
    val flattenedGrid = grid.flatten

    flattenedGrid(maxIndex)
  }
}
