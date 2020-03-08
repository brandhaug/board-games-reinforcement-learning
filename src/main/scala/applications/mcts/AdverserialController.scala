package applications.mcts

import agent.MonteCarloAgent
import applications.mcts.PlayerType.PlayerType
import baseagent.Agent
import environment.adverserial.AdverserialMemory
import environment.adverserial.hex.HexEnvironmentCreator
import environment.adverserial.nim.NimEnvironmentCreator
import environment.{Action, Environment, EnvironmentType}
import environment.adverserial.ledge.{LedgeCellType, LedgeEnvironmentCreator}
import scalafx.Includes._
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control._
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafxml.core.macros.sfxml
import utils.StringUtils

import scala.util.Random

@sfxml
class Controller(pane: Pane,
                 canvas: Canvas,
                 nimEnvironmentRadioButton: RadioButton,
                 ledgeEnvironmentRadioButton: RadioButton,
                 hexEnvironmentRadioButton: RadioButton,
                 boardSizeInput: TextField,
                 secondaryEnvironmentVariableInput: TextField,
                 createEnvironmentButton: Button,
                 trainButton: Button,
                 startButton: Button,
                 resetButton: Button,
                 hardResetButton: Button) {

  // Environment toggle group
  val environmentToggleGroup = new ToggleGroup()
  initializeEnvironmentToggleGroup()

  val gc: GraphicsContext = canvas.graphicsContext2D

  // Global variables
  var timeline: Timeline              = _
  var initialEnvironment: Environment = _

  var agent: Agent = _

  // States
  var paused = true

  initialize()

  def initialize(hardReset: Boolean = true): Unit = {
    if (hardReset) {
      initialEnvironment = initializeEnvironment()
      agent = initializeAgent()
    }

    resetGui()

    render(initialEnvironment)

    var environment = initialEnvironment

    environment.environmentType match {
      case EnvironmentType.Nim   => println(f"Starting pile: ${environment.nonEmptyCells} stones")
      case EnvironmentType.Ledge => println(f"Start board: [${environment.toString}]")
    }

    var playerType = getStartingPlayerType

    timeline = new Timeline {
      cycleCount = Timeline.Indefinite

      keyFrames = Seq(
        KeyFrame(
          Arguments.stepDelay s,
          onFinished = () => {
            if (environment.possibleActions.isEmpty && !paused) {
              toggleStart()
            } else {
              val action = getAction(environment, playerType)

              val nextEnvironment = environment.step(action)
              val nextPlayerType  = PlayerType.getNextPlayerType(playerType)

              printEnvironment(environment, action, playerType, nextEnvironment)

              environment = nextEnvironment
              playerType = nextPlayerType
              render(nextEnvironment)
            }
          }
        ))
    }
  }

  def printEnvironment(environment: Environment, action: Action, playerType: PlayerType, nextEnvironment: Environment): Unit = {
    environment.environmentType match {
      case EnvironmentType.Nim =>
        println(f"P${playerType.id} selects ${action.actionId} stones. Remaining stones = ${nextEnvironment.nonEmptyCells}")
      case EnvironmentType.Ledge =>
        val fromCellIndex = (action.yIndex * environment.board.grid(action.yIndex).size) + action.xIndex
        val fromCell      = environment.board.grid(action.yIndex)(action.xIndex)
        val fromCellType = LedgeCellType(fromCell.cellType) match {
          case LedgeCellType.Copper => "copper"
          case LedgeCellType.Gold   => "gold"
        }
        val actionString = if (action.xIndex == 0 && action.yIndex == 0) {
          f"removes ${fromCellType} from cell ${fromCellIndex}"
        } else {
          f"moves ${fromCellType} from cell ${fromCellIndex} to cell ${action.actionId}"
        }
        println(f"P${playerType.id} ${actionString}: [${environment.toString}]")
    }

    if (nextEnvironment.possibleActions.isEmpty && !paused) {
      println(f"P${playerType.id} wins")
    }
  }

  def train(): Unit = {
    for {
      epoch <- 1 to Arguments.epochs
    } yield {
      trainBatch(epoch)
    }
  }

  def trainBatch(epoch: Int): Unit = {
    val batchHistory = for {
      _ <- (1 to Arguments.batchSize).toList
      startingPlayer = getStartingPlayerType
      environment    = initializeEnvironment()
    } yield {
      playGame(environment, playerType = startingPlayer)
    }

    val winCount = batchHistory.count(memories => memories.last.playerType == PlayerType.Player1)
    println(f"Epoch $epoch/${Arguments.epochs} - Wins: ${winCount}/${batchHistory.size} (${((winCount.toDouble / batchHistory.size.toDouble) * 100).round}%%)")
  }

  def getStartingPlayerType: PlayerType = Arguments.startingPlayerType match {
    case PlayerType.Mixed =>
      val random = Random.nextDouble
      if (random >= 0.5) PlayerType.Player1
      else PlayerType.Player2
    case startingPlayerType => startingPlayerType
  }

  def getAction(environment: Environment, playerType: PlayerType): Action = {
    playerType match {
      case PlayerType.Player1 => agent.act(environment)
      case PlayerType.Player2 => agent.randomAction(environment)
    }
  }

  def playGame(environment: Environment, playerType: PlayerType, memories: List[AdverserialMemory] = List()): List[AdverserialMemory] = {
    if (environment.possibleActions.isEmpty) {
      memories
    } else {
      val action          = getAction(environment, playerType)
      val nextEnvironment = environment.step(action)
      if (Arguments.verbose) printEnvironment(environment, action, playerType, nextEnvironment)
      val memory       = AdverserialMemory(environment, action, nextEnvironment, playerType)
      val nextMemories = memories :+ memory

      val nextPlayerType = PlayerType.getNextPlayerType(playerType)
      playGame(nextEnvironment, playerType = nextPlayerType, memories = nextMemories)
    }
  }

  def render(environment: Environment): Unit = {
    gc.setFill(Color.Black)
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    gc.fillRect(0, 0, canvas.getWidth, canvas.getHeight)
    environment.render(gc)
  }

  def resetGui(): Unit = {
    if (nimEnvironmentRadioButton.selected()) {
      secondaryEnvironmentVariableInput.setPromptText("Max take")
    } else {
      secondaryEnvironmentVariableInput.setPromptText("# Copper coins")
    }
    startButton.setText("Start")
  }

  def initializeEnvironment(): Environment = {
    val sizeInputValue                         = boardSizeInput.getText
    val secondaryEnvironmentVariableInputValue = secondaryEnvironmentVariableInput.getText
    val defaultSize                            = 20
    val defaultSecondaryEnvironmentVariable    = 4
    val size                                   = if (sizeInputValue.nonEmpty && StringUtils.isNumeric(sizeInputValue)) sizeInputValue.toInt else defaultSize
    val secondaryEnvironmentVariable =
      if (secondaryEnvironmentVariableInputValue.nonEmpty && StringUtils.isNumeric(secondaryEnvironmentVariableInputValue)) secondaryEnvironmentVariableInputValue.toInt
      else defaultSecondaryEnvironmentVariable

    if (nimEnvironmentRadioButton.selected()) {
      initializeNimEnvironment(size, maxTake = secondaryEnvironmentVariable)
    } else if (ledgeEnvironmentRadioButton.selected()) {
      initializeLedgeEnvironment(size, copperCount = secondaryEnvironmentVariable)
    } else if (hexEnvironmentRadioButton.selected()) {
      initializeHexEnvironment(size)
    } else {
      throw new Error("Unknown environment selected")
    }
  }

  def initializeHexEnvironment(size: Int): Environment = {
    HexEnvironmentCreator.createEnvironment(size)
  }

  def initializeNimEnvironment(size: Int, maxTake: Int): Environment = {
    if (maxTake >= size) {
      throw new Exception("Max take must be less than boardsize")
    }

    NimEnvironmentCreator.createEnvironment(size, maxTake)
  }

  def initializeLedgeEnvironment(size: Int, copperCount: Int): Environment = {
    if (copperCount >= size) {
      throw new Exception("# Copper coints must be less than boardsize")
    }

    LedgeEnvironmentCreator.createEnvironment(size, copperCount)
  }

  def initializeEnvironmentToggleGroup(): Unit = {
    nimEnvironmentRadioButton.setToggleGroup(environmentToggleGroup)
    ledgeEnvironmentRadioButton.setToggleGroup(environmentToggleGroup)
    hexEnvironmentRadioButton.setToggleGroup(environmentToggleGroup)
    nimEnvironmentRadioButton.setSelected(true)
  }

  def toggleStart(): Unit = {
    paused = !paused

    if (paused) {
      timeline.pause()
      startButton.setText("Start")
    } else {
      timeline.play()
      startButton.setText("Pause")
    }
  }

  def initializeAgent(): Agent = {
    MonteCarloAgent()
  }

  def hardReset(): Unit = {
    paused = true
    timeline.stop()
    initialize()
  }

  def reset(): Unit = {
    paused = true
    timeline.stop()
    initialize(hardReset = false)
  }

  def selectEnvironment(): Unit = {
    if (hexEnvironmentRadioButton.selected()) {
      secondaryEnvironmentVariableInput.setVisible(false)
    } else {
      secondaryEnvironmentVariableInput.setVisible(true)
    }

    hardReset()
  }

  def handleMouseClicked(mouseEvent: MouseEvent): Unit = {
    if (paused) {
      initialEnvironment = initialEnvironment.toggleCell(Math.round(mouseEvent.getX).toInt, Math.round(mouseEvent.getY).toInt)
      reset()
    }
  }
}

object Window {
  val width  = 800
  val height = 800
}
