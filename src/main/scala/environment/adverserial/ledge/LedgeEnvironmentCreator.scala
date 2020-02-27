package environment.adverserial.ledge

import scala.annotation.tailrec
import scala.util.Random

object LedgeEnvironmentCreator {

  def createEnvironment(size: Int, copperCount: Int): LedgeEnvironment = {
    val squareRoot        = Math.sqrt(size.toDouble)
    val roundedSquareRoot = squareRoot.round.toInt

    val copperIndexes = generateCopperIndexes(size, copperCount)
    val goldIndex     = generateGoldIndex(size, copperIndexes)

    val baseGrid = for {
      y <- (0 until roundedSquareRoot).toList
    } yield {
      for {
        x <- (0 until roundedSquareRoot).toList
      } yield {
        LedgeCell(x, y, LedgeCellType.Empty)
      }
    }

    val remainingCellsCount = size - baseGrid.flatten.size

    val moduloRow = if (remainingCellsCount == 0) {
      List.empty
    } else {
      for {
        x <- (0 until roundedSquareRoot).toList
      } yield {
        val y = if (remainingCellsCount > 0) roundedSquareRoot else roundedSquareRoot - 1
        if (remainingCellsCount > 0 && x < remainingCellsCount) LedgeCell(x, y, LedgeCellType.Empty)
        else if (remainingCellsCount < 0 && x < roundedSquareRoot + remainingCellsCount) LedgeCell(x, y, LedgeCellType.Empty)
        else LedgeCell(x, roundedSquareRoot + 1, LedgeCellType.None)
      }
    }

    val gridWithModulo = if (remainingCellsCount == 0) {
      baseGrid
    } else if (remainingCellsCount < 0) {
      baseGrid.dropRight(1) :+ moduloRow
    } else {
      baseGrid :+ moduloRow
    }

    val finalGrid = for {
      row <- gridWithModulo
    } yield {
      for {
        cell <- row
        cellIndex = (cell.yIndex * row.size) + cell.xIndex
      } yield {
        if (goldIndex == cellIndex) LedgeCell(cell.xIndex, cell.yIndex, LedgeCellType.Gold)
        else if (copperIndexes.contains(cellIndex)) LedgeCell(cell.xIndex, cell.yIndex, LedgeCellType.Copper)
        else cell
      }
    }

    val board = LedgeBoard(finalGrid)

    LedgeEnvironment(board)
  }

  @tailrec
  private def generateCopperIndexes(size: Int, copperCount: Int, list: List[Int] = List()): List[Int] = {
    if (list.size == copperCount) {
      list
    } else {
      val randomIndex = Random.nextInt(size)

      if (list.contains(randomIndex)) generateCopperIndexes(size, copperCount, list)
      else generateCopperIndexes(size, copperCount, list :+ randomIndex)
    }
  }

  @tailrec
  private def generateGoldIndex(size: Int, list: List[Int]): Int = {
    val randomIndex = Random.nextInt(size)

    if (list.contains(randomIndex)) generateGoldIndex(size, list)
    else randomIndex
  }
}
