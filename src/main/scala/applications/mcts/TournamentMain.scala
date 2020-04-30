package applications.mcts

import java.io.{BufferedReader, PrintStream}

import applications.mcts.agent.StateActionNetwork
import environment.adversarial.hex.HexEnvironment
import javax.net.ssl.SSLSocket
import utils.SocketUtils

import scala.io._

object TournamentMain {

  var stateActionNetwork: StateActionNetwork = _

  def main(args: Array[String]) = {
    val ipAddress = "129.241.113.109"
    val port      = 33000

    val socketFactory = SocketUtils.getFactory

    println("Connecting")
    val socket = socketFactory.createSocket(ipAddress, port).asInstanceOf[SSLSocket]
    val in     = new BufferedSource(socket.getInputStream).bufferedReader()
    val out    = new PrintStream(socket.getOutputStream)
    try {
      println("Connected")
      while (!socket.isClosed) {
        val currentLine = SocketUtils.nextLine(in)

        println("Received: " + currentLine)

        if (currentLine contains "username") {
          out.write("mabrandh".getBytes())
          out.flush()
        } else if (currentLine contains "password") {
          out.write(Pass.password.getBytes())
          out.flush()
        } else if (currentLine contains "Welcome") {
          // Start playing tournament
          playTournament(in, out)
          // Tournament finished, disconnect from server.
          println("Closing")
          socket.close()
        } else if (currentLine contains "Invalid credentials") {
          socket.close()
        } else if (currentLine contains "player-name") {
          // We are asked to enter the name we want for our player in the tournament
          out.write("mabrandh".getBytes())
          out.flush()
        } else if (currentLine contains "qualify") {
          // We are asked if we want to qualify or play a test game
          out.write("n".getBytes())
//          out.write("Y".getBytes())
          out.flush()
        } else if (currentLine contains "stress") {
          out.write("Y".getBytes())
          out.flush()
        } else if (currentLine contains "Sorry") {
          // We have no more qualification attempts remaining
          socket.close()
        } else {
          println("Unknown input")
          socket.close()
        }
      }
      println("Closed")
    } catch {
      case e: Any =>
        println(e)
        socket.close()
        println("Closed")
    }
  }

  def playTournament(in: BufferedReader, out: PrintStream): Unit = {
    var continue = true
    var games = 0
    var wins = 0

    while (continue) {
      val state = SocketUtils.nextLine(in)
      println(f"TournamentState: $state")
      if (state == "Series start") {
        val id             = SocketUtils.nextLine(in).toInt
        val playerIdMap    = SocketUtils.nextLine(in)
        val gamesCount     = SocketUtils.nextLine(in).toInt
        val gameParameters = SocketUtils.nextLine(in).replace("[", "").replace("]", "").split(", ").map(_.toInt).toList
        val boardSize      = if (gameParameters.nonEmpty) gameParameters.head else 5
        val pathName       = AdversarialArguments.getModelPath(size = boardSize)
        stateActionNetwork = StateActionNetwork(boardSize, Some(pathName))
        println(s"ID: ${id}, PlayerIdMap: ${playerIdMap}, GamesCount: ${gamesCount}, GameParams: ${gameParameters}")
      } else if (state == "Game start") {
        val startPlayerValue = SocketUtils.nextLine(in).toInt
        val startPlayerType  = PlayerType(startPlayerValue)
        println(s"StartPlayer: ${startPlayerType}")
      } else if (state == "Game end") {
        val winner   = SocketUtils.nextLine(in).toInt
        if (winner == 1) wins += 1
        games += 1
//        if (games == AdversarialArguments.tournamentGames) {
//          continue = false
//          println(f"Wins ${wins}/${games}")
//        }
        val endState = SocketUtils.nextLine(in)
        println(s"Winner: $winner")
      } else if (state == "Series end") {
        val stats = SocketUtils.nextLine(in)
        println(s"Stats: $stats")
      } else if (state == "Tournament end") {
        val score = SocketUtils.nextLine(in)
        println(s"Score: $score")
        continue = false
      } else if (state == "Illegal action") {
        val gridList      = SocketUtils.nextLine(in).replace("(", "").replace(")", "").split(", ").map(_.toInt).toList
        val illegalAction = SocketUtils.nextLine(in)
        println(s"Illegal action: $illegalAction")
        continue = false
      } else {
        val gridList           = state.replace("(", "").replace(")", "").split(", ").map(_.toInt).toList
        val player = gridList.take(1).head
        val playerType = PlayerType(player)
        val processedGridList  = gridList.drop(1)
        val environment        = HexEnvironment(processedGridList)
        println(environment.toString)
        val selectedActionCell = stateActionNetwork.predictActionCell(environment.board.grid, playerType)
        val actionString       = f"(${selectedActionCell.yIndex}, ${selectedActionCell.xIndex})"
        out.write(actionString.getBytes())
        out.flush()
      }
    }
  }

  case class SeriesStatistics(id: Int, seriesId: Int, wins: Int, losses: Int)
}
