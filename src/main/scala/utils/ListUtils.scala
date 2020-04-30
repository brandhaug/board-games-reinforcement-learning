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

  def getRow[A](list: List[A], size: Int, y: Int): List[A] = {
    for {
    x <- (0 until size).toList
    index = y * size + x
    } yield {
      list(index)
    }
  }

  def deflatten[A](values: List[A], size: Int): List[List[A]] = {
    for {
      y <- (0 until size).toList
      row = getRow(values, size, y)
    } yield {
      row
    }
  }

  def normalizeGrid(values: List[List[Double]]): List[List[Double]] = {
    val flattened = values.flatten
    val normalized = normalize(flattened, min = Some(0.0), max = Some(2.0))
    val size = values.size
    deflatten(normalized, size)
  }

  def normalize(values: List[Double], min: Option[Double] = None, max: Option[Double] = None): List[Double] = {
    val maxValue = if (max.isEmpty) values.max else max.get
    val minValue = if (min.isEmpty) values.min else min.get

    if (maxValue == minValue) { // Empty board
      values.map(_ => 0.0)
    } else {
      values.map(value => (value - minValue) / (maxValue - minValue))
    }
  }
}
