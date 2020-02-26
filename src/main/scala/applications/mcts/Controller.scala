package applications.mcts

import agent._
import applications.mcts.PlayerType.StartingPlayerType
import applications.mcts.agent.MonteCarloAgent
import environment.nim.NimEnvironmentCreator
import environment.{Environment, Memory}
import environment.ledge.LedgeEnvironmentCreator
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

  var agent: MonteCarloAgent = _

  // States
  var paused = true

  initialize()

  def initialize(hardReset: Boolean = true): Unit = {
    if (hardReset) {
      initialEnvironment = initializeEnvironment()
      agent = initializeAgent(initialEnvironment)
    }

    resetGui()

    render(initialEnvironment)

    var environment = initialEnvironment

    timeline = new Timeline {
      cycleCount = Timeline.Indefinite
      keyFrames = Seq(
        KeyFrame(
          Arguments.stepDelay s,
          onFinished = () => {
            if (environment.possibleActions.isEmpty && !paused) {
              toggleStart()
            } else {
//              val action = agent.act(environment)
//              println(action.toString)
//              val nextEnvironment = environment.step(action)
//              environment = nextEnvironment
//              render(nextEnvironment)
            }
          }
        ))
    }
  }

  def train(): Unit = {
    val environment = initialEnvironment
    for {
      episode <- 1 to Arguments.epochs
    } yield {
      println(f"Episode $episode/${Arguments.epochs}")
      trainBatch(environment)
    }
  }

  def trainBatch(environment: Environment): Unit = {
    val startingPlayer = Arguments.startingPlayerType match {
      case PlayerType.Mixed => {
        val random = Random.nextDouble
        if (random >= 0.5) PlayerType.Player1
        else PlayerType.Player2
      }
      case startingPlayerType => startingPlayerType
    }

    val batchHistory = for {
      episode <- (1 to Arguments.batchSize).toList
    } yield {
      println(f"Game $episode/${Arguments.batchSize}")
      playGame(environment, playerType = startingPlayer)
    }

    agent = agent.train(batchHistory)
  }

  def playGame(environment: Environment, playerType: StartingPlayerType, memories: List[Memory] = List()): List[Memory] = {
    if (environment.possibleActions.isEmpty) {
      memories
    } else {
      val action = agent.act(environment)
      val nextEnvironment = environment.step(action)
      val memory = Memory(environment, action, nextEnvironment)
      val nextMemories = memories :+ memory

      val nextPlayerType = playerType match {
        case PlayerType.Player1 => PlayerType.Player2
        case PlayerType.Player2 => PlayerType.Player1
      }
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
    } else {
      initializeLedgeEnvironment(size, copperCount = secondaryEnvironmentVariable)
    }
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

  def initializeAgent(environment: Environment): MonteCarloAgent = {
    MonteCarloAgent(initialEnvironment)
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
