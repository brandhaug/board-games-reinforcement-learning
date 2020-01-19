package environment.pegsolitaire

import java.io.File

import environment.pegsolitaire.enums.{PegBoardType, PegCellType}

import scala.io.Source

object PegSolitaireFileReader {
  def readFile(selectedFile: File): PegSolitaire = {
    val lines: List[String] = Source.fromFile(selectedFile).getLines.toList
    val first :: rest = lines

    val boardType = first match {
      case "square" => PegBoardType.Square
      case "hex"    => PegBoardType.Hex
      case _        => PegBoardType.Square
    }

    val grid = for {
      line <- rest
      lineList = line.split("").toList
    } yield {
      lineList.map {
        case "-" => PegCell(PegCellType.None, boardType)
        case "1" => PegCell(PegCellType.Peg, boardType)
        case "0" => PegCell(PegCellType.Empty, boardType)
        case _   => PegCell(PegCellType.None, boardType)
      }
    }

    val pegBoard = PegBoard(grid, boardType)
    PegSolitaire(pegBoard)
  }
}
