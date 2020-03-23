package base

import environment.{Cell, Environment}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.factory.Nd4j

trait Network {
  val initialEnvironment: Environment
  val channels: Int
  val miniBatchSize: Int
  val model: MultiLayerNetwork

  def normalize(flattenedValues: List[Double]): List[Double] = {
    val max = flattenedValues.max
    val min = flattenedValues.min

    if (max == min) { // Empty board
      flattenedValues.map(_ => 0.0)
    } else {
      flattenedValues.map(value => (value - min) / (max - min))
    }
  }

  def flattenValues(grid: List[List[Cell]]): List[Double] = {
    val values = grid.map(_.map(_.cellType.toDouble))
    values.flatten
  }

  def preprocessInput(grid: List[List[Cell]]): INDArray = {
    val flattenedValues  = flattenValues(grid)
    val normalizedValues = normalize(flattenedValues)
    val indArray         = Nd4j.create(normalizedValues.toArray)
    indArray.reshape(Array(1, normalizedValues.size))
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
