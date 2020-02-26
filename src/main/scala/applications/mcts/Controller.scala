package applications.mcts

import applications.actorcritic.agent._
import environment.nim.NimEnvironmentCreator
import environment.Environment
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

  // States
  var paused = true

  initialize()

  def initialize(hardReset: Boolean = true): Unit = {
    if (hardReset) {
      initialEnvironment = initializeEnvironment()
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
//              val action = applications.actorcritic.agent.act(environment)
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
//    pegsLeftHistory = (for {
//      episode <- 1 to Arguments.episodes
//      memories = playEpisode(environment)
//      _ = if (memories.nonEmpty) println(f"Training: $episode / ${Arguments.episodes}, Reward: ${memories.last.nextEnvironment.reward}, ${applications.actorcritic.agent.toString}")
//      _ = updateRates()
//    } yield {
//      if (memories.isEmpty) {
//        println(f"No possible actions")
//        0
//      } else {
//        memories.last.nextEnvironment.pegsLeft
//      }
//    }).toList
  }

  def playEpisode(environment: Environment, memories: List[Memory] = List.empty): List[Memory] = {
//    if (environment.possibleActions.isEmpty) {
//      applications.actorcritic.agent = applications.actorcritic.agent.resetEligibilities()
//      memories
//    } else {
//      val action = applications.actorcritic.agent.act(environment)
//      val nextEnvironment = environment.step(action)
//      val memory = Memory(environment, action, nextEnvironment)
//      val nextMemories = memories :+ memory
//      applications.actorcritic.agent = applications.actorcritic.agent.train(nextMemories)
//      playEpisode(nextEnvironment, nextMemories)
//    }
    ???
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
