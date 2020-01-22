package main

import java.io.File

import environment.Environment
import environment.pegsolitaire.{PegSolitaire, PegSolitaireFileReader}
import agent.enums.AgentType.AgentType
import agent.enums.AgentType
import environment.enums.EnvironmentType
import scalafx.animation.AnimationTimer
import scalafx.scene.canvas.{Canvas, GraphicsContext}
import scalafx.scene.control.{Button, ComboBox, Label, RadioButton, ToggleGroup}
import scalafx.scene.layout.{Pane, VBox}
import scalafxml.core.macros.sfxml

import scala.util.Random

@sfxml
class Controller(val canvas: Canvas,
                 val vBoxMenu: VBox,
                 val tableLookupRadioButton: RadioButton,
                 val neuralNetworkRadioButton: RadioButton,
                 val comboBox: ComboBox[String],
                 val startButton: Button,
                 val resetButton: Button) {
  val mapsDirectoryName        = "boards"
  val files: List[File]        = listFiles(mapsDirectoryName)
  val fileNames: List[String]  = files.map(file => file.getName).sorted
  var selectedFileName: String = initializeFileSelector(fileNames)
  val gc: GraphicsContext      = canvas.graphicsContext2D

  // Algorithm radio buttons
  val algorithmToggleGroup = new ToggleGroup()
  tableLookupRadioButton.setToggleGroup(algorithmToggleGroup)
  neuralNetworkRadioButton.setToggleGroup(algorithmToggleGroup)
  tableLookupRadioButton.setSelected(true)
  var selectedAgentType: AgentType = AgentType.TableLookup

  var animationTimer: AnimationTimer = _

  // States
  var paused = true

  initialize()

  def initialize(): Unit = {
    initializeGui()

    var previousEnvironment: Environment = initializeEnvironment()
    previousEnvironment.render(gc)
    //    val agent = Agent(selectedAgentType, initialState)

    animationTimer = AnimationTimer(_ => {
      if (!paused && previousEnvironment.possibleActions.nonEmpty) {
        val actionIndex = Random.nextInt(previousEnvironment.possibleActions.length)
        val action = previousEnvironment.possibleActions(actionIndex) //agent.act(previousEnvironment)
        val currentEnvironment: Environment = previousEnvironment.step(action)

        previousEnvironment = currentEnvironment
        render(currentEnvironment)
      }
    })
    animationTimer.start()
  }

  def render(environment: Environment): Unit = {
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    environment.render(gc)
    //    generationLabel.setText("Generation: " + generation)
    //    makeSpanLabel.setText("Make Span: " + bestMakeSpan)
  }

  def initializeGui(): Unit = {
    startButton.setText("Start")
    comboBox.setVisible(true)
    //    generationLabel.setText("Generation: -")
    //    makeSpanLabel.setText("Make span: -")
  }

  def initializeEnvironment(): Environment = {
    val selectedFile = files.find(file => file.getName == selectedFileName).get

    Arguments.environmentType match {
      case EnvironmentType.PegSolitaire =>
        PegSolitaireFileReader.readFile(selectedFile)
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

  def toggleAlgorithm(): Unit = {
    selectedAgentType = {
      if (tableLookupRadioButton.selected()) AgentType.TableLookup
      else if (neuralNetworkRadioButton.selected()) AgentType.NeuralNetwork
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
      startButton.setText("Start")
    } else {
      startButton.setText("Pause")
    }
  }

  def reset(): Unit = {
    paused = true
    animationTimer.stop()
    gc.clearRect(0, 0, canvas.getWidth, canvas.getHeight)
    initialize()
  }
}

object Canvas {
  val width  = 800
  val height = 800
}
