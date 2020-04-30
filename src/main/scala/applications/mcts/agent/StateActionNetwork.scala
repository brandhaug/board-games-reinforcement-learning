package applications.mcts.agent

import java.io.File

import applications.mcts.{AdversarialArguments, PlayerType}
import applications.mcts.PlayerType.PlayerType
import base.Network
import environment.{Cell, Environment}
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.inputs.InputType
import org.deeplearning4j.nn.conf.layers.{ConvolutionLayer, DenseLayer, OutputLayer}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.nd4j.linalg.activations.Activation
import utils.ListUtils

object StateActionNetwork {
  def apply(initialEnvironment: Environment, pathName: Option[String] = None): StateActionNetwork = {
    StateActionNetwork(initialEnvironment.board.grid.size, pathName)
  }
}

case class StateActionNetwork(size: Int, pathName: Option[String]) extends Network {
  val channels: Int = 1

  val model: MultiLayerNetwork = {
    val inputHeight     = size
    val inputWidth      = size
    val outputDimension = size * size

    val builder = new NeuralNetConfiguration.Builder()
      .weightInit(WeightInit.XAVIER)
      .optimizationAlgo(AdversarialArguments.networkOptimizer)
      .updater(AdversarialArguments.networkUpdater)
      .list()

    for (hiddenLayerConfig <- AdversarialArguments.networkHiddenLayerConfigs) {
      builder.layer(
        hiddenLayerConfig.layerType match {
          case HiddenLayerType.Dense =>
            new DenseLayer.Builder()
              .nOut(hiddenLayerConfig.dimension)
              .activation(hiddenLayerConfig.activation)
              .build()
          case HiddenLayerType.Convolutional =>
            new ConvolutionLayer.Builder()
              .nOut(hiddenLayerConfig.dimension)
              .kernelSize(2, 2)
              .stride(1, 1)
              .activation(hiddenLayerConfig.activation)
              .build()
          case _ => throw new Error("Unknown HiddenLayerType")
        }
      )
    }

    val conf = builder
      .layer(
        new OutputLayer.Builder(AdversarialArguments.networkLoss)
          .nOut(outputDimension)
          .activation(Activation.SOFTMAX)
          .build())
      .setInputType(InputType.feedForward((size * size) + 1))
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

  override def predict(grid: List[List[Cell]], playerType: PlayerType): List[Double] = {
    val input      = preprocessInput(grid, playerType)
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

  def predictActionCell(grid: List[List[Cell]], playerType: PlayerType): Cell = {
    val predictions = predict(grid, playerType)
    val maxIndex = predictions.indexOf(predictions.max)
    val flattenedGrid = grid.flatten
    flattenedGrid(maxIndex)
  }
}
