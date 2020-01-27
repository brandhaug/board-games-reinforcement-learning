package utils

object StringUtils {
  def isNumeric(str: String): Boolean = str.forall(_.isDigit)
}
