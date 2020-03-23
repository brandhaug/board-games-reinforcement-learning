package utils

object ListUtils {
  def sumList(xs: List[Int]): Int = {
    if (xs.isEmpty) 0
    else xs.head + sumList(xs.tail)
  }

  def sumList(xs: List[Double]): Double = {
    if (xs.isEmpty) 0.0
    else xs.head + sumList(xs.tail)
  }

  def softMax(values: List[Double]): List[Double] = {
    val sum = ListUtils.sumList(values.map(value => Math.exp(value)))
    values.map(value => Math.exp(value)/ sum)
  }
}
