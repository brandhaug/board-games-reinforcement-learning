package environment.solo.pegsolitaire

import java.io.File

import environment.BoardType.BoardType
import environment.{BoardType, Environment}

import scala.io.Source

object PegEnvironmentCreator {

  def createEnvironmentFromFile(file: File): PegEnvironment = {
    val source              = Source.fromFile(file)
    val lines: List[String] = source.getLines.toList
    source.close()
    createEnvironmentFromLines(lines)
  }

  def createEnvironment(boardType: BoardType, size: Int): PegEnvironment = {
    val pegId: String = "1"
    val emptyId: String = "0"
    val noneId: String = "-"

    val gridLines = boardType match {
      case BoardType.Square =>
        (for {
          _ <- 1 to size
        } yield {
          List.fill(size)(pegId).mkString("")
        }).toList
      case BoardType.Diamond =>
        (for {
          _ <- 1 to size
        } yield {
          List.fill(size)(pegId).mkString("")
        }).toList
      case BoardType.Triangular =>
        (for {
          y <- 1 to size
        } yield {
          (List.fill(y)(pegId) ++ List.fill(size - y)(noneId)).mkString("")
        }).toList

    }

    createEnvironmentFromGridLines(gridLines, boardType)
  }

  def createEnvironmentFromLines(lines: List[String]): PegEnvironment = {
    val headerLine :: gridLines = lines

    val boardType = headerLine match {
      case "square"     => BoardType.Square
      case "triangular" => BoardType.Triangular
      case "diamond"    => BoardType.Diamond
      case _            => throw new Exception("Unknown board type")
    }

    createEnvironmentFromGridLines(gridLines, boardType)
  }

  def createEnvironmentFromGridLines(lines: List[String], boardType: BoardType): PegEnvironment = {
    val grid = for {
      (line, y) <- lines.zipWithIndex
      lineList = line.split("").toList
      gridRow  = extractGridRowFromLine(lineList, y, boardType)
    } yield {
      gridRow
    }

    val pegBoard = PegBoard(grid, boardType)
    PegEnvironment(pegBoard)
  }

  private def extractGridRowFromLine(lineList: List[String], y: Int, boardType: BoardType): List[PegCell] = {
    for {
      (cellString, x) <- lineList.zipWithIndex
    } yield {
      cellString match {
        case "1" => PegCell(x, y, PegCellType.Peg, boardType)
        case "0" => PegCell(x, y, PegCellType.Empty, boardType)
        case "-" => PegCell(x, y, PegCellType.None, boardType)
        case _   => throw new Exception("Unknown char in line")
      }
    }
  }
}
