package pegsolitaire

import java.io.File

import scala.io.Source

object MapReader {
  def readFile(selectedFile: File): Board = {
    val lines: List[String] = Source.fromFile(selectedFile).getLines.toList
    val first :: rest = lines

    val boardType = first match {
      case "square" => BoardType.Square
      case "hex"    => BoardType.Hex
      case _        => BoardType.Square
    }

    val boardGrid = for {
      line <- rest
      lineList = line.split("").toList
    } yield {
      lineList.map {
        case "-" => Cell(CellType.None)
        case "1" => Cell(CellType.Peg)
        case "0" => Cell(CellType.Empty)
        case _   => Cell(CellType.None)
      }
    }

    Board(boardGrid, boardType)
  }
}
