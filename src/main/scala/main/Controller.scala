package main

import java.io.File

import scalafx.animation.AnimationTimer
import scalafx.scene.control.{Button, ComboBox, Label, RadioButton, ToggleGroup}
import scalafx.scene.layout.{Pane, VBox}
import scalafxml.core.macros.sfxml

import scala.collection.JavaConverters._

@sfxml
class Controller(val pane: Pane,
                 val vBoxMenu: VBox,
                 val comboBox: ComboBox[String],
                 val startButton: Button,
                 val resetButton: Button) {
  val mapsDirectoryName = "maps"
  val files: List[File] = listFiles(mapsDirectoryName)
  val fileNames: List[String] = files.map(file => file.getName).sorted
  var selectedFileName: String = initializeFileSelector(fileNames)

  // Algorithm radio buttons
//  val algorithmToggleGroup = new ToggleGroup()
//  psoRadioButton.setToggleGroup(algorithmToggleGroup)
//  baRadioButton.setToggleGroup(algorithmToggleGroup)
//  psoRadioButton.setSelected(true)
//  var selectedAlgorithm: AlgorithmEnum = AlgorithmEnum.ParticleSwarmOptimization

  var animationTimer: AnimationTimer = _

  // States
  var paused = true

  initialize()

  def initialize(): Unit = {
    initializeGui()
//    val (jobs, machines): (Seq[Job], Seq[Machine]) = ProblemFileReader.readFile(directoryName, selectedFileName)
//    val jssp: JSSP                                 = new JSSP(jobs, machines, selectedAlgorithm)

//    var previousState: Option[State] = None

    animationTimer = AnimationTimer(
      _ => {
        if (!paused) {
//          val currentState: State = jssp.tick(previousState)

//          previousState = Some(currentState)
//          render(currentState.generation, currentState.bestSchedule, currentState.bestMakeSpan, machines)
        }
      }
    )
    animationTimer.start()
  }

  def render(): Unit = { // (generation: Int, bestSchedule: Seq[OperationTimeSlot], bestMakeSpan: Int, machines: Seq[Machine]
//    generationLabel.setText("Generation: " + generation)
//    makeSpanLabel.setText("Make Span: " + bestMakeSpan)
  }


  def initializeGui(): Unit = {
    startButton.setText("Start")
    comboBox.setVisible(true)
//    generationLabel.setText("Generation: -")
//    makeSpanLabel.setText("Make span: -")
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

  /**
   * ScalaFX functions
   */
//  def toggleAlgorithm(): Unit = {
//    selectedAlgorithm = {
//      if (psoRadioButton.selected()) AlgorithmEnum.ParticleSwarmOptimization
//      else if (baRadioButton.selected()) AlgorithmEnum.BeesAlgorithm
//      else throw new Error("Error in algorithm radio buttons")
//    }
//
//    reset()
//  }

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
    pane.children.clear()
    initialize()
  }
}