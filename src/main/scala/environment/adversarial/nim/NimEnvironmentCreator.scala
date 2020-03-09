package environment.adversarial.nim

object NimEnvironmentCreator {

  def createEnvironment(size: Int, maxTake: Int, emptyCells: Int = 0): NimEnvironment = {
    val squareRoot = Math.sqrt(size.toDouble)

    val roundedSquareRoot = squareRoot.round.toInt

    val baseGrid = for {
      y <- (0 until roundedSquareRoot).toList
    } yield {
      for {
        x <- (0 until roundedSquareRoot).toList
      } yield {
        NimCell(x, y, NimCellType.Peg)
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
        if (remainingCellsCount > 0 && x < remainingCellsCount) NimCell(x, y, NimCellType.Peg)
        else if (remainingCellsCount < 0 && x < roundedSquareRoot + remainingCellsCount) NimCell(x, y, NimCellType.Peg)
        else NimCell(x, roundedSquareRoot + 1, NimCellType.None)
      }
    }

    val gridWithModulo = if (remainingCellsCount == 0) {
      baseGrid
    } else if (remainingCellsCount < 0) {
      baseGrid.dropRight(1) :+ moduloRow
    } else {
      baseGrid :+ moduloRow
    }

    val gridWithEmptyCells = if (emptyCells == 0) {
      gridWithModulo
    } else {
      for {
        (row, yIndex) <- gridWithModulo.zipWithIndex
      } yield {
        for {
          (cell, xIndex) <- row.zipWithIndex
          cellIndex = (yIndex * row.size) + xIndex
        } yield {
          if (cellIndex < emptyCells) NimCell(cell.xIndex, cell.yIndex, NimCellType.Empty) else cell
        }
      }
    }

    val board = NimBoard(gridWithEmptyCells)

    NimEnvironment(board, maxTake)
  }
}
