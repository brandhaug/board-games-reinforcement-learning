package environment.adversarial.hex

object HexEnvironmentCreator {

  def createEnvironment(size: Int): HexEnvironment = {
    val grid = for {
      y <- (0 until size).toList
      row = createRow(size, y)
    } yield {
      row
    }

    val board = HexBoard(grid)
    HexEnvironment(board)
  }

  private def createRow(size: Int, y: Int): List[HexCell] = {
    for {
      x <- (0 until size).toList
    } yield {
      HexCell(x, y, HexCellType.Empty)
    }
  }
}
