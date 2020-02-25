package applications.mcts

import applications.actorcritic.agent._
import environment.nim.NimEnvironmentCreator
import environment.Environment
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
                 boardMaxTakeInput: TextField,
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
  var timeline: Timeline = _
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
    startButton.setText("Start")
  }

  def initializeEnvironment(): Environment = {
    if (nimEnvironmentRadioButton.selected()) {
      initializeNimEnvironment()
    } else {
      initializeLedgeEnvironment()
    }
  }

  def initializeNimEnvironment(): Environment = {
    val sizeInputValue = boardSizeInput.getText
    val maxTakeInputValue = boardMaxTakeInput.getText
    val defaultSize = 20
    val defaultMaxTake = 4
    val size = if (sizeInputValue.nonEmpty && StringUtils.isNumeric(sizeInputValue)) sizeInputValue.toInt else defaultSize
    val maxTake = if (maxTakeInputValue.nonEmpty && StringUtils.isNumeric(maxTakeInputValue)) maxTakeInputValue.toInt else defaultMaxTake

    if (maxTake >= size) {
      throw new Exception("Max take must be less than boardsize")
    }

    NimEnvironmentCreator.createEnvironment(size, maxTake)
  }

  def initializeLedgeEnvironment(): Environment = {
    ???
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
