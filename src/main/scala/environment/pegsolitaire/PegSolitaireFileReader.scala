package environment.pegsolitaire

import java.io.File

import environment.BoardType.BoardType
import environment.{BoardType, Environment}

import scala.io.Source

object PegSolitaireFileReader {

  def readFile(file: File): Environment = {
    val source              = Source.fromFile(file)
    val lines: List[String] = source.getLines.toList
    source.close()

    val first :: rest = lines

    val boardType = first match {
      case "square" => BoardType.Square
      case "triangular"    => BoardType.Triangular
      case "diamond" => BoardType.Diamond
      case _        => throw new Exception("Unknown board type")
    }

    val grid = for {
      (line, y) <- rest.zipWithIndex
      lineList = line.split("").toList
      gridRow  = extractGridRowFromLine(lineList, y, boardType)
    } yield {
      gridRow
    }

    val pegBoard = PegBoard(grid, boardType)
    PegSolitaire(pegBoard)
  }

  def extractGridRowFromLine(lineList: List[String], y: Int, boardType: BoardType): List[PegCell] = {
    for {
      (cellString, x) <- lineList.zipWithIndex
    } yield {
      cellString match {
        case "-" => PegCell(x, y, PegCellType.None, boardType)
        case "1" => PegCell(x, y, PegCellType.Peg, boardType)
        case "0" => PegCell(x, y, PegCellType.Empty, boardType)
        case _   => PegCell(x, y, PegCellType.None, boardType)
      }
    }
  }
}
