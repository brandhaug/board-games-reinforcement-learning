package utils

import scala.util.Random

object ListUtils {
  def sum(xs: List[Int]): Int = {
    if (xs.isEmpty) 0
    else xs.head + sum(xs.tail)
  }

  def sum(values: List[Double]): Double = {
    if (values.isEmpty) 0.0
    else values.head + sum(values.tail)
  }

  def softMax(values: List[Double]): List[Double] = {
    val normalizedValues = normalize(values)
    val normalizedSum = sum(normalizedValues.map(value => Math.exp(value)))
    normalizedValues.map(value => Math.exp(value) / normalizedSum)
  }

  def takeRandomBatch[A](list: List[A], batchSize: Int): List[A] = {
    Random.shuffle(list).take(batchSize)
  }

  def getRow(list: List[Double], size: Int, y: Int): List[Double] = {
    for {
    x <- (0 until size).toList
    index = y * size + x
    } yield {
      list(index)
    }
  }

  def normalizeGrid(values: List[List[Double]]): List[List[Double]] = {
    val flattened = values.flatten
    val normalized = normalize(flattened)
    val size = values.size
    for {
      y <- (0 until size).toList
      row = getRow(normalized, size, y)
    } yield {
      row
    }
  }

  def normalize(values: List[Double]): List[Double] = {
    val max = values.max
    val min = values.min

    if (max == min) { // Empty board
      values.map(_ => 0.0)
    } else {
      values.map(value => (value - min) / (max - min))
    }
  }
}
