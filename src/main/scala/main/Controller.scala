package main

import java.io.File

import agent.{Agent, AgentType, Memory, NetworkAgent, RandomAgent, TableAgent}
import environment.{BoardType, Environment, EnvironmentType}
import agent.AgentType.AgentType
import environment.BoardType.BoardType
import environment.pegsolitaire.PegSolitaireFileReader
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Button, ComboBox, Label, RadioButton, TextField, ToggleGroup}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.scene.input.MouseEvent
import scalafx.scene.paint.Color
import utils.StringUtils

@sfxml
class Controller(canvas: Canvas,
                 fileEnvironmentRadioButton: RadioButton,
                 customEnvironmentRadioButton: RadioButton,
                 fileComboBox: ComboBox[String],
                 customBoardTypeComboBox: ComboBox[BoardType],
                 customBoardSizeInput: TextField,
                 createCustomEnvironmentButton: Button,
                 tableLookupRadioButton: RadioButton,
                 neuralNetworkRadioButton: RadioButton,
                 randomRadioButton: RadioButton,
                 infoLabel: Label,
                 startButton: Button) {
  // Selected file
  val files: List[File]       = listFiles(Arguments.mapsDirectoryName)
  val fileNames: List[String] = files.map(file => file.getName).sorted

  // Environment toggle group
  val agentToggleGroup = new ToggleGroup()
  initializeAgentToggleGroup()

  // Agent toggle group
  val environmentToggleGroup = new ToggleGroup()
  initializeEnvironmentToggleGroup()

  // Combo boxes
  initializeFileComboBox(fileNames)
  initializeCustomBoardTypeComboBox()
  selectEnvironmentSource()

  // Canvas
  val gc: GraphicsContext = canvas.graphicsContext2D

  // Global variables
  var timeline: Timeline              = _
  var agent: Agent                    = _
  var initialEnvironment: Environment = _

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

    if (memories.isEmpty) {
      println(f"No possible actions")
    } else {
      println(f"Final reward: ${memories.last.environment.reward}")

      agent = agent.train(memories)
      if (episode != Arguments.episodes) train(episode + 1)
    }
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
      case _ =>
        canvas.setRotate(0)
        canvas.setScaleX(1)
        canvas.setScaleY(1)
    }
  }

  def initializeEnvironment(): Environment = {
    if (customEnvironmentRadioButton.selected()) {
      initializeCustomEnvironment()
    } else {
      initializeEnvironmentFromFile()
    }
  }

  def initializeEnvironmentFromFile(): Environment = {
    val selectedFileName: String = Option(fileComboBox.getValue) match {
      case Some(value) => value
      case None        => fileNames.head
    }
    val selectedFile = files.find(file => file.getName == selectedFileName).get

    Arguments.environmentType match {
      case EnvironmentType.PegSolitaire => PegSolitaireFileReader.createEnvironmentFromFile(selectedFile)
      case _                            => throw new Exception("Unknown EnvironmentType")
    }
  }

  def initializeCustomEnvironment(): Environment = {
    val inputValue       = customBoardSizeInput.getText
    val defaultBoardSize = 5
    val boardType        = customBoardTypeComboBox.getValue
    val boardSize        = if (StringUtils.isNumeric(inputValue)) inputValue.toInt else defaultBoardSize
    Arguments.environmentType match {
      case EnvironmentType.PegSolitaire => PegSolitaireFileReader.createEnvironment(boardType, boardSize)
      case _                            => throw new Exception("Unknown EnvironmentType")
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

  def initializeFileComboBox(fileNames: List[String]): Unit = {
    fileNames.foreach(fileName => fileComboBox += fileName)
    val selectedFileName = fileNames.head
    fileComboBox.getSelectionModel.select(fileNames.indexOf(selectedFileName))
  }

  def initializeCustomBoardTypeComboBox(): Unit = {
    BoardType.values.foreach(boardType => customBoardTypeComboBox += boardType)
    val selectedBoardType = BoardType.Square
    customBoardTypeComboBox.getSelectionModel.select(BoardType.values.toList.indexOf(selectedBoardType))
  }

  def createCustomEnvironment(): Unit = {
    hardReset()
  }

  def initializeAgentToggleGroup(): Unit = {
    tableLookupRadioButton.setToggleGroup(agentToggleGroup)
    neuralNetworkRadioButton.setToggleGroup(agentToggleGroup)
    randomRadioButton.setToggleGroup(agentToggleGroup)
    tableLookupRadioButton.setSelected(true)
  }

  def initializeEnvironmentToggleGroup(): Unit = {
    fileEnvironmentRadioButton.setToggleGroup(environmentToggleGroup)
    customEnvironmentRadioButton.setToggleGroup(environmentToggleGroup)
    fileEnvironmentRadioButton.setSelected(true)
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
    hardReset()
  }

  def selectFile(): Unit = {
    hardReset()
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

  def selectEnvironmentSource(): Unit = {
    if (fileEnvironmentRadioButton.selected()) {
      fileComboBox.setVisible(true)
      customBoardTypeComboBox.setVisible(false)
      customBoardSizeInput.setVisible(false)
      createCustomEnvironmentButton.setVisible(false)
    } else if (customEnvironmentRadioButton.selected()) {
      fileComboBox.setVisible(false)
      customBoardTypeComboBox.setVisible(true)
      customBoardSizeInput.setVisible(true)
      createCustomEnvironmentButton.setVisible(true)
    }
  }

  def handleMouseClicked(mouseEvent: MouseEvent): Unit = {
    if (paused) {
      initialEnvironment = initialEnvironment.toggleCell(Math.round(mouseEvent.getX).toInt, Math.round(mouseEvent.getY).toInt)
      reset()
    }
  }
}

object Canvas {
  val width  = 800
  val height = 800
}
