package base

import applications.mcts.AdversarialArguments
import applications.mcts.agent.HiddenLayerType
import environment.{Cell, Environment}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j
import utils.ListUtils

trait Network {
  val size: Int
  val channels: Int
  val model: MultiLayerNetwork

  def flattenValues(grid: List[List[Cell]]): List[Double] = {
    val values = getGridValues(grid)
    values.flatten
  }

  def getGridValues(grid: List[List[Cell]]): List[List[Double]] = {
    grid.map(_.map(_.cellType.toDouble))
  }

  def preprocessInput(grid: List[List[Cell]]): INDArray = {
    val layerType = AdversarialArguments.networkHiddenLayerConfigs.head.layerType

    layerType match {
      case HiddenLayerType.Dense =>
        val flattenedValues  = flattenValues(grid)
        val normalizedValues = ListUtils.normalize(flattenedValues)
        val indArray = Nd4j.create(normalizedValues.toArray)
        indArray.reshape(Array(1, normalizedValues.size))
      case HiddenLayerType.Convolutional =>
        val values = getGridValues(grid)
        val normalizedValues = ListUtils.normalizeGrid(values)
        val indArray = Nd4j.create(normalizedValues.map(_.toArray).toArray)
        indArray.reshape(Array(1, 1, normalizedValues.size, normalizedValues.size))
    }
  }

  def preprocessLabel(labels: List[Double]): INDArray = {
    val indArray = Nd4j.create(labels.toArray)
    indArray.reshape(Array(1, labels.size))
  }

  def fit(grid: List[List[Cell]], label: Double): Unit = {
    fit(grid, List(label))
  }

  def fit(grid: List[List[Cell]], labels: List[Double]): Unit = {
    val input              = preprocessInput(grid)
    val preprocessedLabels = preprocessLabel(labels)
    model.fit(input, preprocessedLabels)
  }

  def predict(grid: List[List[Cell]]): List[Double] = {
    val input = preprocessInput(grid)
    model.output(input).toDoubleVector.toList
  }

  override def toString: String = model.numParams().toString
}
