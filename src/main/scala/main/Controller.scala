package main

import java.io.File

import agent.{Agent, AgentType, Memory, NetworkAgent, RandomAgent, TableAgent}
import environment.{ActionType, Environment, EnvironmentType}
import agent.AgentType.AgentType
import filereader.PegSolitaireFileReader
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Button, ComboBox, Label, RadioButton, ToggleGroup}
import scalafx.scene.layout.VBox
import scalafxml.core.macros.sfxml
import scalafx.Includes._

import scala.io.Source

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
  val files: List[File]        = listFiles(Arguments.mapsDirectoryName)
  val fileNames: List[String]  = files.map(file => file.getName).sorted
  var selectedFileName: String = initializeFileSelector(fileNames)

  // Canvas
  val gc: GraphicsContext      = canvas.graphicsContext2D

  // Algorithm radio buttons
  val algorithmToggleGroup = new ToggleGroup()
  tableLookupRadioButton.setToggleGroup(algorithmToggleGroup)
  neuralNetworkRadioButton.setToggleGroup(algorithmToggleGroup)
  randomRadioButton.setToggleGroup(algorithmToggleGroup)
  tableLookupRadioButton.setSelected(true)
  var selectedAgentType: AgentType = AgentType.TableLookup

  // Global variables
  var timeline: Timeline = _
  var agent: Agent       = _
  var initialEnvironment: Environment = _

  // States
  var paused = true

  initialize()

  def initialize(): Unit = {
    initializeGui()

    initialEnvironment = initializeEnvironment()
    agent = initializeAgent(initialEnvironment)
    initialEnvironment.render(gc)

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
              val action             = agent.act(environment)
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
      val action             = agent.act(environment)
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
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    environment.render(gc)
  }

  def initializeGui(): Unit = {
    startButton.setText("Start")
    comboBox.setVisible(true)
  }

  def initializeEnvironment(): Environment = {
    val selectedFile = files.find(file => file.getName == selectedFileName).get

    Arguments.environmentType match {
      case EnvironmentType.PegSolitaire =>
        PegSolitaireFileReader.readFile(selectedFile)
    }
  }

  def initializeAgent(environment: Environment): Agent = {
    val actionTypes = Arguments.environmentType match {
      case EnvironmentType.PegSolitaire => List(ActionType.Left, ActionType.Right, ActionType.Up, ActionType.Down)
    }

    selectedAgentType match {
      case AgentType.TableLookup   => TableAgent(environment, actionTypes)
      case AgentType.NeuralNetwork => NetworkAgent(environment, actionTypes)
      case AgentType.Random        => RandomAgent(environment, actionTypes)
    }
  }

  def initializeFileSelector(fileNames: List[String]): String = {
    fileNames.foreach(fileName => comboBox += fileName)
    val selectedFileName = fileNames.head
    comboBox.getSelectionModel.select(fileNames.indexOf(selectedFileName))
    selectedFileName
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

  def selectAgentType(): Unit = {
    selectedAgentType = {
      if (tableLookupRadioButton.selected()) AgentType.TableLookup
      else if (neuralNetworkRadioButton.selected()) AgentType.NeuralNetwork
      else if (randomRadioButton.selected()) AgentType.Random
      else throw new Error("Error in algorithm radio buttons")
    }

    reset()
  }

  def selectFile(): Unit = {
    selectedFileName = comboBox.getValue.toString
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
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    initialize()
  }

}

object Canvas {
  val width  = 800
  val height = 800
}
