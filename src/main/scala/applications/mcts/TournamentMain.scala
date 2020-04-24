package applications.mcts

import java.io.{BufferedInputStream, FileInputStream, _}
import java.nio.ByteBuffer
import java.security.KeyStore
import java.security.cert.{Certificate, CertificateFactory}

import applications.mcts.agent.{MonteCarloNetworkAgent, StateActionNetwork}
import environment.Environment
import environment.adversarial.hex.HexEnvironment
import javax.net.ssl.{SSLContext, SSLSocket, SSLSocketFactory, TrustManagerFactory}

import scala.io._

object TournamentMain {

  def nextLine(in: BufferedReader): String = {
    val chars = new Array[Char](1024)
    in.read(chars)
    chars.mkString("").trim
  }

  def main(args: Array[String]) = {
    val ipAddress = "129.241.113.109"
    val port      = 33000

    val socketFactory = getFactory()

    println("Connecting")
    val socket = socketFactory.createSocket(ipAddress, port).asInstanceOf[SSLSocket]
    val in     = new BufferedSource(socket.getInputStream).bufferedReader()
    val out    = new PrintStream(socket.getOutputStream)
    try {
      println("Connected")
      while (!socket.isClosed) {
        val currentLine = nextLine(in)

        println("Received: " + currentLine)

        if (currentLine contains "username") {
          out.write("mabrandh".getBytes())
          out.flush()
        } else if (currentLine contains "password") {
          out.write("".getBytes())
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

  def getFactory(): SSLSocketFactory = {
    val certificateFactory     = CertificateFactory.getInstance("X.509")
    val certificateInputStream = new FileInputStream("src/main/scala/server.crt")

    val certificateBufferedInputStream = new BufferedInputStream(certificateInputStream)
    var certificate: Certificate       = null
    try {
      certificate = certificateFactory.generateCertificate(certificateBufferedInputStream)
    } finally {
      certificateBufferedInputStream.close()
    }

    val keyStoreType = KeyStore.getDefaultType
    val keyStore     = KeyStore.getInstance(keyStoreType)
    keyStore.load(null, null)
    keyStore.setCertificateEntry("ca", certificate)

    val trustManagerFactoryAlgorithm = TrustManagerFactory.getDefaultAlgorithm
    val trustManagerFactory          = TrustManagerFactory.getInstance(trustManagerFactoryAlgorithm)
    trustManagerFactory.init(keyStore)

    val context = SSLContext.getInstance("TLS")
    context.init(null, trustManagerFactory.getTrustManagers, null)
    context.getSocketFactory
  }

  def playTournament(in: BufferedReader, out: PrintStream): Unit = {

    /**
      * This is the main loop of an actor-server connection. It plays a tournament against the server and will receive
      * a final score for your actor in the end. A tournament consists of three processes.
      *
      *             - A game: A game is a single round of HEX played against one of the players on the server.
      *
      *             - A series: A series consists of several games against the same player on the server. This is to ensure that a
      * fair score against this player is calculated.
      *
      *             - A tournament: A tournament consists of all the series played against a collection of players on the server.
      * This will result in a final score for your actor, based on how well it did in the tournament.
      *
      * When a tournament is played the actor will receive messages from the server and generate responses based on
      * what these message are. There are SEVEN possible messages:
      *
      *             - Series start: A new series is starting and your actor is given several pieces of information.
      *             - Game start: A new game is starting, and you are informed of which player will make the first move.
      *             - Game end: The current game has ended and you receive basic information about the result.
      *             - Series end: A series has ended and you will receive basic summarizing statistics.
      *             - Tournament end; A tournament has ended. When a tournament is ended, you will receive your final score. You
      * should save this score and verify that it is the same score you are given in our system at the
      * end of the server-client week.
      *             - Illegal action: The previous action your actor tried to execute was evaluated to be an illegal action by the
      *                               server. The server sends you the previous state and the attempted action by your actor.
      * Finally the server will close down the connection from its end. You should analye the
      * previous state and your attempted action and try to find the bug in your action generator.
      *             - Request action: The server will request actions from your actor. You will receive a state tuple, representing
      * the current state of the board, and you will use your actor to find the next move. This move
      * will then be sent to the server; it must be a move tuple, (row, col), which represent the next
      * empty cell in which your actor wants to place a piece.
      */


    var continue = true
    while(continue) {
      val state = nextLine(in)
      println(f"TournamentState: $state")
      if (state == "Series start") {
        val id = nextLine(in).toInt
        val playerIdMap = nextLine(in)
        val gamesCount = nextLine(in).toInt
        val gameParameters = nextLine(in).replace("[", "").replace("]", "").split(", ").map(_.toInt).toList
        println(s"ID: ${id}, PlayerIdMap: ${playerIdMap}, GamesCount: ${gamesCount}, GameParams: ${gameParameters}")
      } else if (state == "Game start") {
        val startPlayerValue = nextLine(in).toInt
        val startPlayerType = PlayerType(startPlayerValue)
        println(s"StartPlayer: ${startPlayerType}")
      } else if (state == "Game end") {
        val winner = nextLine(in).toInt
        val endState = nextLine(in)
        println(s"Winner: $winner")
      } else if (state == "Series end") {
        val stats = nextLine(in)
        println(s"Stats: $stats")
      } else if (state == "Tournament end") {
        val score = nextLine(in)
        println(s"Score: $score")
        continue = false
      } else if (state == "Illegal action") {
        val gridList = nextLine(in).replace("(", "").replace(")", "").split(", ").map(_.toInt).toList
        val illegalAction = nextLine(in)
        println(s"Illegal action: $illegalAction")
        continue = false
      } else {
        handleGetAction(in, out, state)
      }
    }
  }

  def handleGetAction(in: BufferedReader, out: PrintStream, state: String): Unit = {
    val gridList           = state.replace("(", "").replace(")", "").split(", ").map(_.toInt).toList
    val processedGridList  = gridList.drop(1)
    val environment        = HexEnvironment(processedGridList)
    val pathName           = AdversarialArguments.getModelPath(size = environment.board.grid.size)
    val stateActionNetwork = StateActionNetwork(environment, Some(pathName))
    val selectedActionCell = stateActionNetwork.predictActionCell(environment.board.grid)
    val actionString       = f"(${selectedActionCell.yIndex}, ${selectedActionCell.xIndex})"
    out.write(actionString.getBytes())
    out.flush()
    playTournament(in, out)
  }

  case class SeriesStatistics(id: Int, seriesId: Int, wins: Int, losses: Int)
}
