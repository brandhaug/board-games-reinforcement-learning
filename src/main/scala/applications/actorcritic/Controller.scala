package applications.actorcritic

import java.io.File

import applications.actorcritic.agent.{Agent, AgentType, Memory, NetworkAgent, StateValueNetwork, TableAgent}
import environment.{BoardType, Environment, EnvironmentType}
import applications.actorcritic.agent.AgentType.AgentType
import environment.BoardType.BoardType
import environment.pegsolitaire.PegEnvironmentCreator
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Button, ComboBox, Label, RadioButton, TextField, ToggleGroup}
import scalafxml.core.macros.sfxml
import scalafx.Includes._
import scalafx.collections.ObservableBuffer
import scalafx.scene.chart.{LineChart, NumberAxis, XYChart}
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import utils.StringUtils

@sfxml
class Controller(pane: Pane,
                 canvas: Canvas,
                 fileEnvironmentRadioButton: RadioButton,
                 customEnvironmentRadioButton: RadioButton,
                 fileComboBox: ComboBox[String],
                 customBoardTypeComboBox: ComboBox[BoardType],
                 customBoardSizeInput: TextField,
                 createCustomEnvironmentButton: Button,
                 tableLookupRadioButton: RadioButton,
                 neuralNetworkRadioButton: RadioButton,
                 infoLabel: Label,
                 trainButton: Button,
                 startButton: Button,
                 resetButton: Button,
                 hardResetButton: Button,
                 showChartButton: Button) {
  // Selected file
  val files: List[File]       = listFiles(Arguments.mapsDirectoryName)
  val fileNames: List[String] = files.map(file => file.getName).sorted

  // Agent toggle group
  val agentToggleGroup = new ToggleGroup()
  initializeAgentToggleGroup()

  // Environment toggle group
  val environmentToggleGroup = new ToggleGroup()
  initializeEnvironmentToggleGroup()

  // Combo boxes
  initializeFileComboBox(fileNames)
  initializeCustomBoardTypeComboBox()
  selectEnvironmentSource()

  val gc: GraphicsContext = canvas.graphicsContext2D

  // Global variables
  var timeline: Timeline              = _
  var agent: Agent                    = _
  var initialEnvironment: Environment = _
  var pegsLeftHistory: List[Int]      = _

  // States
  var paused = true

  initialize()

  def initialize(hardReset: Boolean = true): Unit = {
    if (hardReset) {
      initialEnvironment = initializeEnvironment()
      agent = initializeAgent(initialEnvironment)
      showChartButton.setVisible(false)
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
              println(action.toString)
              val nextEnvironment = environment.step(action)
              environment = nextEnvironment
              render(nextEnvironment)
            }
          }
        ))
    }
  }

  def train(): Unit = {
    val environment = initialEnvironment
    pegsLeftHistory = (for {
      episode <- 1 to Arguments.episodes
      memories = playEpisode(environment)
      _        = if (memories.nonEmpty) println(f"Training: $episode / ${Arguments.episodes}, Reward: ${memories.last.nextEnvironment.reward}, ${agent.toString}")
      _        = updateRates()
    } yield {
      if (memories.isEmpty) {
        println(f"No possible actions")
        0
      } else {
        memories.last.nextEnvironment.pegsLeft
      }
    }).toList
    showChartButton.setVisible(true)
  }

  def updateRates(): Unit = {
    agent = agent.updateEpsilonRate()
  }

  def playEpisode(environment: Environment, memories: List[Memory] = List.empty): List[Memory] = {
    if (environment.possibleActions.isEmpty) {
      agent = agent.resetEligibilities()
      memories
    } else {
      val action          = agent.act(environment)
      val nextEnvironment = environment.step(action)
      val memory          = Memory(environment, action, nextEnvironment)
      val nextMemories = memories :+ memory
      agent = agent.train(nextMemories)
      playEpisode(nextEnvironment, nextMemories)
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
      case EnvironmentType.PegSolitaire => PegEnvironmentCreator.createEnvironmentFromFile(selectedFile)
      case _                            => throw new Exception("Unknown EnvironmentType")
    }
  }

  def initializeCustomEnvironment(): Environment = {
    val inputValue       = customBoardSizeInput.getText
    val defaultBoardSize = 5
    val boardType        = customBoardTypeComboBox.getValue
    val boardSize        = if (inputValue.nonEmpty && StringUtils.isNumeric(inputValue)) inputValue.toInt else defaultBoardSize
    Arguments.environmentType match {
      case EnvironmentType.PegSolitaire => PegEnvironmentCreator.createEnvironment(boardType, boardSize)
      case _                            => throw new Exception("Unknown EnvironmentType")
    }

  }

  def initializeAgent(environment: Environment): Agent = {
    selectedAgentType() match {
      case AgentType.TableLookup   => TableAgent(environment)
      case AgentType.NeuralNetwork =>
        val stateValueNetwork = StateValueNetwork(environment)
        NetworkAgent(environment, stateValueNetwork = stateValueNetwork)
      case _                       => throw new Exception("Unknown applications.actorcritic.agent")
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
    else throw new Exception("No applications.actorcritic.agent radio button selected")
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
      agent = agent.removeEpsilon()
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

  def toggleChart(): Unit = {
    if (showChartButton.getText == "Show chart") {
      canvas.setVisible(false)
      initializeChart()
    } else {
      removeChartFromPane()
      initialize(hardReset = false)
    }
  }

  def removeChartFromPane(): Unit = {
    pane.children.remove(1, 2)
    trainButton.setVisible(true)
    startButton.setVisible(true)
    resetButton.setVisible(true)
    hardResetButton.setVisible(true)
    canvas.setVisible(true)
    showChartButton.setText("Show chart")
  }

  def initializeChart(): Unit = {
    trainButton.setVisible(false)
    startButton.setVisible(false)
    resetButton.setVisible(false)
    hardResetButton.setVisible(false)
    val xAxis = new NumberAxis()
    xAxis.label = "Episode"
    val yAxis     = new NumberAxis()
    val lineChart = LineChart(xAxis, yAxis)
    lineChart.title = "Pegs left"
    lineChart.setPrefHeight(Window.height)
    lineChart.setPrefWidth(Window.width)

    val data = ObservableBuffer(pegsLeftHistory.zipWithIndex map {
      case (pegsLeft, episode) => XYChart.Data[Number, Number](episode, pegsLeft)
    })

    val series = XYChart.Series[Number, Number]("Pegs left", data)
    lineChart.getData.add(series)
    lineChart.setLegendVisible(false);
    lineChart.setCreateSymbols(false)

    pane.children.add(lineChart)
    showChartButton.setText("Close chart")
  }
}

object Window {
  val width  = 800
  val height = 800
}
