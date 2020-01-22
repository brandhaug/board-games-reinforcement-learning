package environment.pegsolitaire

import java.io.File

import environment.Environment
import environment.pegsolitaire.enums.PegBoardType.PegBoardType
import environment.pegsolitaire.enums.{PegBoardType, PegCellType}

import scala.io.Source
import scala.util.Random

object PegSolitaireFileReader {

  def readFile(selectedFile: File): Environment = {
    val lines: List[String] = Source.fromFile(selectedFile).getLines.toList
    val first :: rest = lines

    val boardType = first match {
      case "square" => PegBoardType.Square
      case "hex"    => PegBoardType.Hex
      case _        => PegBoardType.Square
    }

    val grid = for {
      (line, y) <- rest.zipWithIndex
      lineList = line.split("").toList
      gridRow = extractGridRowFromLine(lineList, y, boardType)
    } yield {
      gridRow
    }

    val pegBoard = PegBoard(grid, boardType)
    PegSolitaire(pegBoard)
  }

  def extractGridRowFromLine(lineList: List[String],
                             y: Int,
                             boardType: PegBoardType): List[PegCell] = {
    for {
      (cellString, x) <- lineList.zipWithIndex
    } yield {
      cellString match {
        case "-" => PegCell(Random.nextLong().toString, x, y, PegCellType.None, boardType)
        case "1" => PegCell(Random.nextLong().toString, x, y, PegCellType.Peg, boardType)
        case "0" => PegCell(Random.nextLong().toString, x, y, PegCellType.Empty, boardType)
        case _   => PegCell(Random.nextLong().toString, x, y, PegCellType.None, boardType)
      }
    }
  }
}
