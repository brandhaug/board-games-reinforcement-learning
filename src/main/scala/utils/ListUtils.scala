package utils

object ListUtils {
  def sumList(xs: List[Int]): Int = {
    if (xs.isEmpty) 0
    else xs.head + sumList(xs.tail)
  }
}
