package main

import java.io.File

import agent.{Agent, AgentType, Memory, NetworkAgent, RandomAgent, TableAgent}
import environment.{BoardType, Environment, EnvironmentType}
import agent.AgentType.AgentType
import environment.pegsolitaire.PegSolitaireFileReader
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Button, ComboBox, Label, RadioButton, ToggleGroup}
import scalafx.scene.layout.VBox
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.scene.paint.Color

@sfxml
class Controller(val canvas: Canvas,
                 val vBoxMenu: VBox,
                 val tableLookupRadioButton: RadioButton,
                 val neuralNetworkRadioButton: RadioButton,
                 val randomRadioButton: RadioButton,
                 val infoLabel: Label,
                 val comboBox: ComboBox[String],
                 val trainButton: Button,
                 val startButton: Button,
                 val resetButton: Button) {
  // Selected file
  val files: List[File]       = listFiles(Arguments.mapsDirectoryName)
  val fileNames: List[String] = files.map(file => file.getName).sorted
  initializeFileSelector(fileNames)

  // Canvas
  val gc: GraphicsContext = canvas.graphicsContext2D

  // Agent toggle group
  val agentToggleGroup = new ToggleGroup()
  initializeAgentToggleGroup()

  // Global variables
  var timeline: Timeline              = _
  var agent: Agent                    = _
  var initialEnvironment: Environment = _

  // States
  var paused = true

  initialize()

  def initialize(): Unit = {
    initialEnvironment = initializeEnvironment()
    resetGui()
    agent = initializeAgent(initialEnvironment)
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
              val action          = agent.act(environment)
              val nextEnvironment = environment.step(action)
              environment = nextEnvironment
              render(nextEnvironment)
            }
          }
        ))
    }
  }

  def train(): Unit = {
    train(1)
  }

  def train(episode: Int): Unit = {
    println(f"Training: $episode / ${Arguments.episodes}")
    var environment = initialEnvironment
    var memories    = List.empty[Memory]

    while (environment.possibleActions.nonEmpty) {
      val action          = agent.act(environment)
      val nextEnvironment = environment.step(action)
      val nextMemories    = memories :+ Memory(environment, action, nextEnvironment)
      environment = nextEnvironment
      memories = nextMemories
    }

    println(f"Final reward: ${memories.last.environment.reward}")

    agent = agent.train(memories)
    if (episode != Arguments.episodes) train(episode + 1)
  }

  def render(environment: Environment): Unit = {
    gc.setFill(Color.Black)
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    gc.fillRect(0, 0, canvas.getWidth, canvas.getHeight)
    environment.render(gc)

  }

  def resetGui(): Unit = {
    startButton.setText("Start")
    resetCanvas()
  }

  def resetCanvas(): Unit = {
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    initialEnvironment.board.boardType match {
      case BoardType.Diamond =>
        canvas.setRotate(45)
        canvas.setScaleX(0.7)
        canvas.setScaleY(0.7)
      case _                 =>
        canvas.setRotate(0)
        canvas.setScaleX(1)
        canvas.setScaleY(1)
    }
  }

  def initializeEnvironment(): Environment = {
    val selectedFileName: String = Option(comboBox.getValue) match {
      case Some(value) => value
      case None        => fileNames.head
    }
    val selectedFile = files.find(file => file.getName == selectedFileName).get

    Arguments.environmentType match {
      case EnvironmentType.PegSolitaire =>
        PegSolitaireFileReader.readFile(selectedFile)
    }
  }

  def initializeAgent(environment: Environment): Agent = {
    selectedAgentType() match {
      case AgentType.TableLookup   => TableAgent(environment)
      case AgentType.NeuralNetwork => NetworkAgent(environment)
      case AgentType.Random        => RandomAgent(environment)
      case _                       => throw new Exception("Unknown agent")
    }
  }

  def initializeFileSelector(fileNames: List[String]): Unit = {
    fileNames.foreach(fileName => comboBox += fileName)
    val selectedFileName = fileNames.head
    comboBox.getSelectionModel.select(fileNames.indexOf(selectedFileName))
    comboBox.setVisible(true)
  }

  def initializeAgentToggleGroup(): Unit = {
    tableLookupRadioButton.setToggleGroup(agentToggleGroup)
    neuralNetworkRadioButton.setToggleGroup(agentToggleGroup)
    randomRadioButton.setToggleGroup(agentToggleGroup)
    tableLookupRadioButton.setSelected(true)
  }

  def listFiles(directoryName: String): List[File] = {
    val path   = getClass.getResource(directoryName)
    val folder = new File(path.getFile)
    if (folder.exists && folder.isDirectory) {
      folder.listFiles.toList
    } else {
      List[File]()
    }
  }

  def selectedAgentType(): AgentType = {
    if (tableLookupRadioButton.selected()) AgentType.TableLookup
    else if (neuralNetworkRadioButton.selected()) AgentType.NeuralNetwork
    else if (randomRadioButton.selected()) AgentType.Random
    else throw new Exception("No agent radio button selected")
  }

  def selectAgentType(): Unit = {
    reset()
  }

  def selectFile(): Unit = {
    reset()
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

  def reset(): Unit = {
    paused = true
    timeline.stop()
    initialize()
  }

}

object Canvas {
  val width  = 800
  val height = 800
}
